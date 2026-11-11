package org.aston.homeworkThree.service;

import org.aston.homeworkThree.dao.UserDAO;
import org.aston.homeworkThree.model.Contact;
import org.aston.homeworkThree.model.User;
import org.aston.homeworkThree.util.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UserDAOImp implements UserDAO {
    private final SessionFactory sessionFactory;

    public UserDAOImp() {
        this.sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    }

    public UserDAOImp(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void createUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(user);
                transaction.commit();
            } catch (Exception ex) {
                rollbackIfActive(transaction);
                throw new RuntimeException("Ошибка добавления пользователя в БД.", ex);
            }
        }
    }

    @Override
    public Optional<User> updateUser(long id, User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User updateUser = session.get(User.class, id);
                if (updateUser == null) {
                    transaction.rollback();
                    return Optional.empty();
                }
                updateUser.setFirstName(user.getFirstName());
                updateUser.setLastName(user.getLastName());
                transaction.commit();
                return Optional.of(updateUser);
            } catch (Exception ex) {
                rollbackIfActive(transaction);
                throw new RuntimeException("Ошибка при обновлении данных пользователя в БД.", ex);
            }
        }
    }

    @Override
    public Optional<User> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка при поиске пользователя.", ex);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                List<User> users = session.createQuery("FROM User", User.class).list();
                transaction.commit();
                return users;
            } catch (Exception ex) {
                rollbackIfActive(transaction);
                throw new RuntimeException("Ошибка при формировании списка всех пользователей.", ex);
            }
        }
    }

    @Override
    public List<Contact> findAllContactUser(long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User user = session.get(User.class, id);
                List<Contact> contacts = user.getContactList();
                transaction.commit();
                return contacts;
            } catch (Exception ex) {
                rollbackIfActive(transaction);
                throw new RuntimeException("Ошибка при формировании всех контактов пользователя.", ex);
            }
        }
    }

    @Override
    public boolean removeUser(long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User user = session.get(User.class, id);
                if (user == null) {
                    transaction.rollback();
                    return false;
                }
                session.remove(user);
                transaction.commit();
                return true;
            } catch (Exception ex) {
                rollbackIfActive(transaction);
                throw new RuntimeException("Ошибка при удалении пользователя.", ex);
            }
        }
    }

    private void rollbackIfActive(Transaction transaction) {
        if (transaction.isActive()) {
            transaction.rollback();
        }
    }
}
