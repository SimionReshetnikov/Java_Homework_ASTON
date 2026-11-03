package org.aston.homeworkTwo.service;

import org.aston.homeworkTwo.dao.UserDAO;
import org.aston.homeworkTwo.model.User;
import org.aston.homeworkTwo.util.HibernateSessionFactoryUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class UserService implements UserDAO {

    @Override
    public Optional<User> findById(long id) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка при поиске пользователя по id = " + id, ex);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка при получении списка пользователей", ex);
        }
    }

    @Override
    public boolean saveUser(User user) {
        return false;
    }

    @Override
    public User updateUser(long id, User updateUser) {
        return null;
    }

    @Override
    public void deleteUser(long id) {

    }
}
