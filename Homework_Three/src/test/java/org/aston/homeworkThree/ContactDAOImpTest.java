package org.aston.homeworkThree;

import org.aston.homeworkThree.model.CategoryContact;
import org.aston.homeworkThree.model.Contact;
import org.aston.homeworkThree.model.User;
import org.aston.homeworkThree.service.ContactDAOImp;
import org.aston.homeworkThree.service.UserDAOImp;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Testcontainers
public class ContactDAOImpTest {
    @Container
    static PostgreSQLContainer<?> postgres =  new PostgreSQLContainer<>("postgresql:15-alpine")
            .withDatabaseName("aston_homework_three_test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    private static SessionFactory sessionFactory;
    private ContactDAOImp contactDAOImp;
    private UserDAOImp userDAOImp;

    private static Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show-sql", true);

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
        contactDAOImp = new ContactDAOImp(sessionFactory);
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
    void testSaveAndFindContact() {
        Contact contact = new Contact("John", "+1(234)-567-89-01");
        User user = new User("Michael", "Jackson");
        CategoryContact categoryContact = new CategoryContact("Работа");

        userDAOImp.saveUser(user);
        contactDAOImp.saveContact(contact, user.getId(), categoryContact.getId());
        Optional<Contact> contactOpt = contactDAOImp.findById(contact.getId());

        assertTrue(contactOpt.isPresent());
        assertEquals(contact.getUser(), contactOpt.get().getUser());
        assertEquals(contact.getNumber(), contactOpt.get().getContactName());
        assertEquals(contact.getNumber(), contactOpt.get().getNumber());
    }
}
