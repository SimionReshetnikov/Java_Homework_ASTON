package org.aston.homeworkThree;


import org.aston.homeworkThree.model.CategoryContact;
import org.aston.homeworkThree.model.Contact;
import org.aston.homeworkThree.model.User;
import org.aston.homeworkThree.service.UserDAOImp;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserDAOImpTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("aston_homework_three_test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    private static SessionFactory sessionFactory;
    private UserDAOImp userDAOImp;

    private static Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show_sql", "true");

        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Contact.class);
        configuration.addAnnotatedClass(CategoryContact.class);

        return configuration;
    }

    @BeforeAll
    static void init() {
        Configuration configuration = getConfiguration();

        sessionFactory = configuration.buildSessionFactory(
                new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build()
        );
    }

    @BeforeEach
    void setUp() {
        userDAOImp = new UserDAOImp(sessionFactory);
    }

    @AfterAll
    static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @AfterEach
    void clearData() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE users, contact, category_contact RESTART IDENTITY CASCADE")
                    .executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void testSaveAndFindUser() {
        User user = new User("Michael", "Jackson");

        userDAOImp.saveUser(user);
        Optional<User> findUser = userDAOImp.findById(user.getId());

        assertFalse(findUser.isEmpty());
        assertEquals(user.getFirstName(), findUser.get().getFirstName());
        assertEquals(user.getLastName(), findUser.get().getLastName());
    }

    @Test
    void testUpdateAndFindUser() {
        User user = new User("Michael", "Jackson");
        User updateUser = new User("Michael", "JacksonFive");

        userDAOImp.saveUser(user);
        Optional<User> updated = userDAOImp.updateUser(user.getId(), updateUser);

        assertTrue(updated.isPresent());
        assertEquals(updateUser.getFirstName(), updated.get().getFirstName());
        assertEquals(updateUser.getLastName(), updated.get().getLastName());
    }

    @Test
    void testUpdateWithNonExistingId() {
        User user = new User("Michael", "Jackson");
        User updateUser = new User("Michael", "JacksonFive");
        long id = 999999L;

        userDAOImp.saveUser(user);
        Optional<User> updated = userDAOImp.updateUser(id, updateUser);

        assertTrue(updated.isEmpty());
    }

    @Test
    void testUpdateWithNullValue() {
        User user = new User("Michael", "Jackson");

        Optional<User> updated = userDAOImp.updateUser(user.getId(), null);

        assertTrue(updated.isEmpty());
    }

    @Test
    void testFindUserById() {
        User user = new User("Michael", "Jackson");

        userDAOImp.saveUser(user);
        Optional<User> findUser = userDAOImp.findById(user.getId());

        assertTrue(findUser.isPresent());
        assertEquals(user.getFirstName(), findUser.get().getFirstName());
        assertEquals(user.getLastName(), findUser.get().getLastName());
    }

    @Test
    void testFindUserByNonExistingId() {
        User user = new User("Michael", "Jackson");
        long id = 999999L;

        userDAOImp.saveUser(user);
        Optional<User> findUser = userDAOImp.findById(id);

        assertTrue(findUser.isEmpty());
    }

    @Test
    void testFindAllUsersFromEmptyTable() {
        List<User> allUsers = userDAOImp.findAll();

        assertTrue(allUsers.isEmpty());
    }

    @Test
    void testFindAllUsersFromTable() {
        User user1 = new User("Michael", "Jackson");
        User user2 = new User("Nil", "Armstrong");
        User user3 = new User("John", "Travolta");
        int size = 3;

        userDAOImp.saveUser(user1);
        userDAOImp.saveUser(user2);
        userDAOImp.saveUser(user3);
        List<User> allUsers = userDAOImp.findAll();

        assertFalse(allUsers.isEmpty());
        assertEquals(size, allUsers.size());
    }

    @Test
    void testRemoveUserById() {
        User user = new User("Michael", "Jackson");

        userDAOImp.saveUser(user);

        assertTrue(userDAOImp.removeUser(user.getId()));
    }

    @Test
    void testRemoveUserByNonDetailsId() {
        User user = new User("Michael", "Jackson");
        long id = 999999L;

        userDAOImp.saveUser(user);

        assertFalse(userDAOImp.removeUser(id));
    }
}
