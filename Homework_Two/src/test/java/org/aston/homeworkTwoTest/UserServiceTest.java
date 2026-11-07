package org.aston.homeworkTwoTest;

import org.aston.homeworkTwo.model.User;
import org.aston.homeworkTwo.service.UserService;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private static SessionFactory sessionFactory;
    private UserService userService;

    @BeforeAll
    static void initSessionFactory() {
        sessionFactory = new Configuration().configure("hibernate-test.cfg.xml")
                .buildSessionFactory();
    }

    @AfterAll
    static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        userService = new UserService(sessionFactory);
    }

    @AfterEach
    void clearUp() {
        userService.findAll().forEach(u -> userService.deleteUser(u.getId()));
    }

    @Test
    void saveUser_returnsUserWithIdAndCreatedAt() {
        User user = new User("Joe", "joe@example.com", 30);

        User saved = userService.saveUser(user);

        assertNotNull(saved.getId(), "id должен быть проставлен после persist");
        assertNotNull(saved.getCreatedAt(), "createdAt должен быть заполнен");
        assertEquals("Joe", saved.getName());
        assertEquals("joe@example.com", saved.getEmail());
        assertEquals(30, saved.getAge());
    }

    @Test
    void saveUser_duplicateEmail_throwsException() {
        userService.saveUser(new User("Joe", "joe@example.com", 30));

        User duplicate = new User("Another", "joe@example.com", 25);
        assertThrows(RuntimeException.class, () -> userService.saveUser(duplicate));
    }

    @Test
    void findById_existingUser_returnsUser() {
        User user = userService.saveUser(new User("Joe", "joe@example.com", 30));

        Optional<User> userOpt = userService.findById(user.getId());

        assertTrue(userOpt.isPresent());
        assertEquals(user.getId(), userOpt.get().getId());
        assertEquals(user.getName(), userOpt.get().getName());
        assertEquals(user.getEmail(), userOpt.get().getEmail());
        assertEquals(user.getAge(), userOpt.get().getAge());
    }

    @Test
    void findById_missingUser_returnsEmpty() {
        long id = 999999L;
        Optional<User> userOpt = userService.findById(id);
        assertFalse(userOpt.isPresent());
    }

    @Test
    void findAll_emptyTable_returnsEmptyList() {
        List<User> userList = userService.findAll();
        assertTrue(userList.isEmpty());
    }

    @Test
    void findAll_returnsAllSavedUsers() {
        userService.saveUser(new User("Joe", "joe@example.com", 30));
        userService.saveUser(new User("Leny", "leny@example.com", 34));
        userService.saveUser(new User("Harry", "harry@example.com", 23));

        List<User> userList = userService.findAll();

        assertFalse(userList.isEmpty());
        assertEquals(3, userList.size());
    }

    @Test
    void updateUser_existingUser_updatesFields() {
        User saved = userService.saveUser(new User("Joe", "joe@example.com", 30));
        User updateUser = new User("Leny", "leny@example.com", 34);

        Optional<User> updated = userService.updateUser(saved.getId(), updateUser);

        assertTrue(updated.isPresent());
        assertEquals(updateUser.getName(), updated.get().getName());
        assertEquals(updateUser.getEmail(), updated.get().getEmail());
        assertEquals(updateUser.getAge(), updated.get().getAge());
    }

    @Test
    void updateUser_missingUser_returnsEmpty() {
        User updateUser = new User("Leny", "leny@example.com", 34);
        long id = 999999L;
        Optional<User> updated = userService.updateUser(id, updateUser);
        assertFalse(updated.isPresent());
    }

    @Test
    void updateUser_duplicateEmail_throwsException(){
        userService.saveUser(new User("Leny", "leny@example.com", 34));
        User saveUserTwo = userService.saveUser(new User("Harry", "harry@example.com", 25));
        User updateUser = new User("Leny", "leny@example.com", 42);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateUser(saveUserTwo.getId(), updateUser));
    }

    @Test
    void deleteUser_existingUser_returnsTrueAndRemoves() {
        User saveUser = userService.saveUser(new User("Leny", "leny@example.com", 34));

        assertTrue(userService.deleteUser(saveUser.getId()));
        assertFalse(userService.findById(saveUser.getId()).isPresent());
    }

    @Test
    void deleteUser_missingUser_returnFalse() {
        long id = 999999L;
        assertFalse(userService.deleteUser(id));
    }
}