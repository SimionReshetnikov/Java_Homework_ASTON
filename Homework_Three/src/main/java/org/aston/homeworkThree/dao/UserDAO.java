package org.aston.homeworkThree.dao;

import org.aston.homeworkThree.model.Contact;
import org.aston.homeworkThree.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    void saveUser(User user);
    Optional<User> updateUser(long id, User user);
    Optional<User> findById(long id);
    List<User> findAll();
    boolean removeUser(long id);
}
