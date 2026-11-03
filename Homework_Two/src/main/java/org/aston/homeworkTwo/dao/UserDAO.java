package org.aston.homeworkTwo.dao;

import org.aston.homeworkTwo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    Optional<User> findById(long id);
    List<User> findAll();
    boolean saveUser(User user);
    Optional<User> updateUser(long id, User updateUser);
    void deleteUser(long id);
}
