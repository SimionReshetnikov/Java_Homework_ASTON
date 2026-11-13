package org.aston.homeworkThree;

import org.aston.homeworkThree.model.CategoryContact;
import org.aston.homeworkThree.model.Contact;
import org.aston.homeworkThree.model.User;
import org.aston.homeworkThree.service.CategoryContactDAOImp;
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

import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
public class ContactDAOImpTest {
    @Container
    static PostgreSQLContainer<?> postgres =  new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("aston_homework_three_test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    private static SessionFactory sessionFactory;
    private ContactDAOImp contactDAOImp;
    private UserDAOImp userDAOImp;
    private CategoryContactDAOImp categoryContactDAOImp;

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
        categoryContactDAOImp = new CategoryContactDAOImp(sessionFactory);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE users, contact, category_contact RESTART IDENTITY CASCADE")
                    .executeUpdate();
            session.getTransaction().commit();
        }
    }

    @AfterAll
    static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    void testSaveAndFindContact() {
        Contact contact = new Contact("John", "+1(234)-567-89-01");
        User user = new User("Michael", "Jackson");
        CategoryContact categoryContact = new CategoryContact("Работа");

        userDAOImp.saveUser(user);
        categoryContactDAOImp.saveCategory(categoryContact);
        contactDAOImp.saveContact(contact, user.getId(), categoryContact.getId());
        Optional<Contact> contactOpt = contactDAOImp.findById(contact.getId());

        assertTrue(contactOpt.isPresent());
        assertEquals(user.getId(), contactOpt.get().getUser().getId());
        assertEquals(contact.getContactName(), contactOpt.get().getContactName());
        assertEquals(contact.getNumber(), contactOpt.get().getNumber());
        assertEquals(categoryContact.getId(), contactOpt.get().getCategoryContact().getId());
    }

    @Test
    void testSaveContactWithNull() {
        User user = new User("Michael", "Jackson");
        CategoryContact categoryContact = new CategoryContact("Работа");

        userDAOImp.saveUser(user);
        categoryContactDAOImp.saveCategory(categoryContact);

        assertThrows(IllegalArgumentException.class,
                () -> contactDAOImp.saveContact(null, user.getId(), categoryContact.getId()));
    }

    @Test
    void testSaveContactWithExistingUser() {
        Contact contact = new Contact("John", "+1(234)-567-89-01");
        CategoryContact categoryContact = new CategoryContact("Работа");
        long userId = 999999L;

        categoryContactDAOImp.saveCategory(categoryContact);

        assertThrows(IllegalArgumentException.class,
                () -> contactDAOImp.saveContact(contact, userId, categoryContact.getId()));
    }

    @Test
    void testSaveContactWithExistingCategoryContact() {
        Contact contact = new Contact("John", "+1(234)-567-89-01");
        User user = new User("Michael", "Jackson");
        long categoryId = 999999L;

        userDAOImp.saveUser(user);

        assertThrows(IllegalArgumentException.class,
                () -> contactDAOImp.saveContact(contact, user.getId(), categoryId));
    }

    @Test
    void testSaveContactWithDuplicateNumber() {
        Contact contact1 = new Contact("John", "+1(234)-567-89-01");
        User user = new User("Michael", "Jackson");
        CategoryContact categoryContact = new CategoryContact("Работа");
        Contact contact2 = new Contact("Steve", "+1(234)-567-89-01");

        userDAOImp.saveUser(user);
        categoryContactDAOImp.saveCategory(categoryContact);
        contactDAOImp.saveContact(contact1, user.getId(), categoryContact.getId());

        assertThrows(RuntimeException.class,
                () -> contactDAOImp.saveContact(contact2, user.getId(), categoryContact.getId()));
    }

    @Test
    void testUpdateContact() {
        Contact contact1 = new Contact("John", "+1(234)-567-89-01");
        User user = new User("Michael", "Jackson");
        CategoryContact categoryContact = new CategoryContact("Работа");
        Contact updateContact = new Contact("John", "+1(234)-567-33-87");
        CategoryContact categoryContactUpdate = new CategoryContact("Друзья");

        userDAOImp.saveUser(user);
        categoryContactDAOImp.saveCategory(categoryContact);
        categoryContactDAOImp.saveCategory(categoryContactUpdate);
        contactDAOImp.saveContact(contact1, user.getId(), categoryContact.getId());
        Optional<Contact> updated = contactDAOImp.updateContact(contact1.getId(), categoryContactUpdate.getId(), updateContact);

        assertTrue(updated.isPresent());
        assertEquals(updateContact.getContactName(), updated.get().getContactName());
        assertEquals(updateContact.getNumber(), updated.get().getNumber());
        assertEquals(categoryContactUpdate.getId(), updated.get().getCategoryContact().getId());
    }

    @Test
    void testUpdateContactNotFound() {
        Contact updateContact = new Contact("John", "+1(234)-567-33-87");
        CategoryContact categoryContactUpdate = new CategoryContact("Друзья");
        long id = 999999L;

        categoryContactDAOImp.saveCategory(categoryContactUpdate);
        Optional<Contact> contact = contactDAOImp.updateContact(id, categoryContactUpdate.getId(), updateContact);

        assertTrue(contact.isEmpty());
    }

    @Test
    void testUpdateContactWithNotExistingCategory() {
        Contact contact1 = new Contact("John", "+1(234)-567-89-01");
        User user = new User("Michael", "Jackson");
        CategoryContact categoryContact = new CategoryContact("Работа");
        Contact updateContact = new Contact("John", "+1(234)-567-33-87");
        long updateCategoryId = 999999L;

        userDAOImp.saveUser(user);
        categoryContactDAOImp.saveCategory(categoryContact);
        contactDAOImp.saveContact(contact1, user.getId(), categoryContact.getId());
        assertThrows(IllegalArgumentException.class,
                () -> contactDAOImp.updateContact(contact1.getId(), updateCategoryId, updateContact));
    }

    @Test
    void testFindContactById() {
        Contact contact = new Contact("John", "+1(234)-567-89-01");
        User user = new User("Michael", "Jackson");
        CategoryContact categoryContact = new CategoryContact("Работа");

        userDAOImp.saveUser(user);
        categoryContactDAOImp.saveCategory(categoryContact);
        contactDAOImp.saveContact(contact, user.getId(), categoryContact.getId());
        Optional<Contact> contactOpt = contactDAOImp.findById(contact.getId());

        assertTrue(contactOpt.isPresent());
        assertEquals(user.getId(), contactOpt.get().getUser().getId());
        assertEquals(contact.getContactName(), contactOpt.get().getContactName());
        assertEquals(contact.getNumber(), contactOpt.get().getNumber());
        assertEquals(categoryContact.getId(), contactOpt.get().getCategoryContact().getId());
    }

    @Test
    void testFindContactByIdNotFound() {
        Contact contact = new Contact("John", "+1(234)-567-89-01");
        User user = new User("Michael", "Jackson");
        CategoryContact categoryContact = new CategoryContact("Работа");
        long id = 999999L;

        userDAOImp.saveUser(user);
        categoryContactDAOImp.saveCategory(categoryContact);
        contactDAOImp.saveContact(contact, user.getId(), categoryContact.getId());
        Optional<Contact> contactOpt = contactDAOImp.findById(id);

        assertTrue(contactOpt.isEmpty());
    }

    @Test
    void testFindAllContactsByUserId() {
        Contact contact1 = new Contact("John", "+1(234)-567-89-01");
        Contact contact2 = new Contact("Elena", "+1(987)-654-32-10");
        Contact contact3 = new Contact("Harry", "+1(975)-864-31-20");
        User user = new User("Michael", "Jackson");
        CategoryContact categoryContact = new CategoryContact("Работа");
        int size = 3;

        userDAOImp.saveUser(user);
        categoryContactDAOImp.saveCategory(categoryContact);
        contactDAOImp.saveContact(contact1, user.getId(), categoryContact.getId());
        contactDAOImp.saveContact(contact2, user.getId(), categoryContact.getId());
        contactDAOImp.saveContact(contact3, user.getId(), categoryContact.getId());
        List<Contact> contactList = contactDAOImp.findAllContactByUserId(user.getId());

        assertFalse(contactList.isEmpty());
        assertEquals(size, contactList.size());
    }

    @Test
    void testFindAllContactsByUserIdWhenEmpty() {
        User user = new User("Michael", "Jackson");
        CategoryContact categoryContact = new CategoryContact("Работа");

        userDAOImp.saveUser(user);
        categoryContactDAOImp.saveCategory(categoryContact);
        List<Contact> contactList = contactDAOImp.findAllContactByUserId(user.getId());

        assertTrue(contactList.isEmpty());
    }

    @Test
    void testFindAllContactsByNonExistingUserId() {
        Contact contact1 = new Contact("John", "+1(234)-567-89-01");
        Contact contact2 = new Contact("Elena", "+1(987)-654-32-10");
        User user = new User("Michael", "Jackson");
        CategoryContact categoryContact = new CategoryContact("Работа");
        long userId = 999999L;

        userDAOImp.saveUser(user);
        categoryContactDAOImp.saveCategory(categoryContact);
        contactDAOImp.saveContact(contact1, user.getId(), categoryContact.getId());
        contactDAOImp.saveContact(contact2, user.getId(), categoryContact.getId());
        List<Contact> contactsList = contactDAOImp.findAllContactByUserId(userId);

        assertTrue(contactsList.isEmpty());
    }

    @Test
    void testDeleteContact() {
        Contact contact = new Contact("John", "+1(234)-567-89-01");
        User user = new User("Michael", "Jackson");
        CategoryContact categoryContact = new CategoryContact("Работа");

        userDAOImp.saveUser(user);
        categoryContactDAOImp.saveCategory(categoryContact);
        contactDAOImp.saveContact(contact, user.getId(), categoryContact.getId());

        assertTrue(contactDAOImp.deleteContact(contact.getId()));
    }

    @Test
    void testDeleteContactNotFound() {
        long contactId = 999999L;

        assertFalse(contactDAOImp.deleteContact(contactId));
    }

    @Test
    void testDeleteContactDoesNotAffectOtherContacts() {
        Contact contact1 = new Contact("John", "+1(234)-567-89-01");
        Contact contact2 = new Contact("Elena", "+1(987)-654-32-10");
        Contact contact3 = new Contact("Harry", "+1(975)-864-31-20");
        User user = new User("Michael", "Jackson");
        CategoryContact categoryContact = new CategoryContact("Работа");
        int size = 2;

        userDAOImp.saveUser(user);
        categoryContactDAOImp.saveCategory(categoryContact);
        contactDAOImp.saveContact(contact1, user.getId(), categoryContact.getId());
        contactDAOImp.saveContact(contact2, user.getId(), categoryContact.getId());
        contactDAOImp.saveContact(contact3, user.getId(), categoryContact.getId());
        boolean isDeleted = contactDAOImp.deleteContact(contact1.getId());
        List<Contact> contactList = contactDAOImp.findAllContactByUserId(user.getId());

        assertTrue(isDeleted);
        assertEquals(size, contactList.size());
    }

    @Test
    void testDeleteContactRemovesFromUserList() {
        User user = new User("Michael", "Jackson");
        CategoryContact categoryContact = new CategoryContact("Работа");
        Contact contact = new Contact("John", "+1(234)-567-89-01");

        userDAOImp.saveUser(user);
        categoryContactDAOImp.saveCategory(categoryContact);
        contactDAOImp.saveContact(contact, user.getId(), categoryContact.getId());

        List<Contact> contactBefore = contactDAOImp.findAllContactByUserId(user.getId());
        assertEquals(1, contactBefore.size());

        boolean isDeleted = contactDAOImp.deleteContact(contact.getId());
        assertTrue(isDeleted);

        List<Contact> contactAfter = contactDAOImp.findAllContactByUserId(user.getId());
        assertTrue(contactAfter.isEmpty());
    }
}
