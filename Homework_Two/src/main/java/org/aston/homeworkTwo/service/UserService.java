package org.aston.homeworkTwo.service;

import org.aston.homeworkTwo.dao.UserDAO;
import org.aston.homeworkTwo.model.User;
import org.aston.homeworkTwo.util.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserService implements UserDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final SessionFactory sessionFactory;

    public UserService() {
        this.sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    }

    public UserService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<User> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception ex) {
            LOGGER.error("Ошибка при поиске пользователя по id = {}", id, ex);
            throw new RuntimeException("Ошибка при поиске пользователя по id = " + id, ex);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception ex) {
            LOGGER.error("Ошибка при получении списка пользователей", ex);
            throw new RuntimeException("Ошибка при получении списка пользователей", ex);
        }
    }

    @Override
    public User saveUser(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return user;
        } catch (Exception ex) {
            rollbackIfPossible(transaction);
            if (isConstraintViolation(ex)) {
                LOGGER.error("Пользователь с таким email уже существует.", ex);
                throw new RuntimeException("Пользователь с таким email уже существует.", ex);
            }
            LOGGER.error("Ошибка при добавлении записи о пользователе.", ex);
            throw new RuntimeException("Ошибка при добавлении записи о пользователе.", ex);
        }
    }

    @Override
    public Optional<User> updateUser(long id, User updateUser) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user == null) {
                transaction.rollback();
                return Optional.empty();
            }
            user.setName(updateUser.getName());
            user.setEmail(updateUser.getEmail());
            user.setAge(updateUser.getAge());
            transaction.commit();
            return Optional.of(user);
        } catch (Exception ex) {
            rollbackIfPossible(transaction);
            if (isConstraintViolation(ex)) {
                LOGGER.error("Пользователь с таким email уже существует.", ex);
                throw new RuntimeException("Пользователь с таким email уже существует.", ex);
            }
            LOGGER.error("Ошибка при изменении записи о пользователе.", ex);
            throw new RuntimeException("Ошибка при изменении записи о пользователе.", ex);
        }
    }

    @Override
    public boolean deleteUser(long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User removeUser = session.get(User.class, id);
            if (removeUser == null) {
                transaction.rollback();
                return false;
            }
            session.remove(removeUser);
            transaction.commit();
            return true;
        } catch (Exception ex) {
            rollbackIfPossible(transaction);
            LOGGER.error("Ошибка при удалении записи о пользователе.", ex);
            throw new RuntimeException("Ошибка при удалении записи о пользователе.", ex);
        }
    }

    private static void rollbackIfPossible(Transaction transaction) {
        if (transaction != null && transaction.getStatus().canRollback()) {
            transaction.rollback();
        }
    }

    private static boolean isConstraintViolation(Throwable ex) {
        Throwable throwable = ex;
        while (throwable != null) {
            if (throwable instanceof ConstraintViolationException) return true;
            throwable = throwable.getCause();
        }
        return false;
    }
}
