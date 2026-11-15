package org.aston.homeworkThree.DAOTest;

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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class CategoryContactDAOImpTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("aston_homework_three_test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    private static SessionFactory sessionFactory;
    private UserDAOImp userDAOImp;
    private ContactDAOImp contactDAOImp;
    private CategoryContactDAOImp categoryContactDAOImp;

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
        contactDAOImp = new ContactDAOImp(sessionFactory);
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
    void testSaveAndFindByIdCategory() {
        CategoryContact categoryContact = new CategoryContact("Работа");

        categoryContactDAOImp.saveCategory(categoryContact);
        Optional<CategoryContact> category = categoryContactDAOImp.findById(categoryContact.getId());

        assertTrue(category.isPresent());
        assertEquals(categoryContact.getCategoryName(), category.get().getCategoryName());
    }

    @Test
    void testSaveCategoryWithNull() {
        CategoryContact categoryContact = null;

        assertThrows(RuntimeException.class,
                () -> categoryContactDAOImp.saveCategory(categoryContact));
    }

    @Test
    void testSaveCategoryWithDuplicateName() {
        CategoryContact categoryContact = new CategoryContact("Работа");

        categoryContactDAOImp.saveCategory(categoryContact);
        assertThrows(RuntimeException.class,
                () -> categoryContactDAOImp.saveCategory(categoryContact));
    }

    @Test
    void testUpdateCategory() {
        CategoryContact categoryContact = new CategoryContact("Работа");
        CategoryContact categoryContactUpdate = new CategoryContact("Друзья");

        categoryContactDAOImp.saveCategory(categoryContact);
        Optional<CategoryContact> updated = categoryContactDAOImp.updateCategory(categoryContact.getId(), categoryContactUpdate);

        assertTrue(updated.isPresent());
        assertEquals(categoryContactUpdate.getCategoryName(), updated.get().getCategoryName());
    }

    @Test
    void testUpdateCategoryNotFound() {
        CategoryContact categoryContact = new CategoryContact("Работа");
        long id = 999999L;

        Optional<CategoryContact> updated = categoryContactDAOImp.updateCategory(id, categoryContact);

        assertTrue(updated.isEmpty());
    }

    @Test
    void testUpdateCategoryWithNull() {
        CategoryContact categoryContact = new CategoryContact("Работа");

        categoryContactDAOImp.saveCategory(categoryContact);
        Optional<CategoryContact> updated = categoryContactDAOImp.updateCategory(categoryContact.getId(), null);

        assertTrue(updated.isEmpty());
    }

    @Test
    void testUpdateCategoryWithDuplicateName() {
        CategoryContact category1 = new CategoryContact("Работа");
        CategoryContact category2 = new CategoryContact("Друзья");
        CategoryContact categoryContactUpdate = new CategoryContact("Работа");

        categoryContactDAOImp.saveCategory(category1);
        categoryContactDAOImp.saveCategory(category2);
        assertThrows(IllegalStateException.class,
                () -> categoryContactDAOImp.updateCategory(category2.getId(), categoryContactUpdate));
    }

    @Test
    void testFindCategoryId() {
        CategoryContact category = new CategoryContact("Работа");

        categoryContactDAOImp.saveCategory(category);
        Optional<CategoryContact> findCategory = categoryContactDAOImp.findById(category.getId());

        assertTrue(findCategory.isPresent());
        assertEquals(category.getCategoryName(), findCategory.get().getCategoryName());
    }

    @Test
    void testFindCategoryByIdNotFound() {
        CategoryContact category = new CategoryContact("Работа");
        Long id = 999999L;

        categoryContactDAOImp.saveCategory(category);
        Optional<CategoryContact> findCategory = categoryContactDAOImp.findById(id);

        assertTrue(findCategory.isEmpty());
    }

    @Test
    void testFindAllCategories() {
        CategoryContact category1 = new CategoryContact("Работа");
        CategoryContact category2 = new CategoryContact("Друзья");
        CategoryContact category3 = new CategoryContact("Родственники");
        int size = 3;

        categoryContactDAOImp.saveCategory(category1);
        categoryContactDAOImp.saveCategory(category2);
        categoryContactDAOImp.saveCategory(category3);
        List<CategoryContact> categoryList = categoryContactDAOImp.findAll();

        assertFalse(categoryList.isEmpty());
        assertEquals(size, categoryList.size());
    }

    @Test
    void testFindAllCategoriesFromEmptyTable() {
        List<CategoryContact> categoryList = categoryContactDAOImp.findAll();

        assertTrue(categoryList.isEmpty());
    }

    @Test
    void testDeleteCategory() {
        CategoryContact category1 = new CategoryContact("Работа");

        categoryContactDAOImp.saveCategory(category1);
        boolean isDeleted = categoryContactDAOImp.deleteCategory(category1.getId());
        Optional<CategoryContact> currentCategory = categoryContactDAOImp.findById(category1.getId());

        assertTrue(isDeleted);
        assertTrue(currentCategory.isEmpty());
    }

    @Test
    void testDeleteCategoryNotFound() {
        CategoryContact category1 = new CategoryContact("Работа");
        long id = 999999L;

        categoryContactDAOImp.saveCategory(category1);
        boolean isDeleted = categoryContactDAOImp.deleteCategory(id);

        assertFalse(isDeleted);
    }

    @Test
    void testDeleteCategoryWithLinkedContacts() {
        User user = new User("Michael", "Jackson");
        CategoryContact category = new CategoryContact("Работа");
        Contact contact = new Contact("Leny", "+1(234)-567-89-10");

        userDAOImp.saveUser(user);
        categoryContactDAOImp.saveCategory(category);
        contactDAOImp.saveContact(contact, user.getId(), category.getId());

        assertThrows(IllegalStateException.class,
                () -> categoryContactDAOImp.deleteCategory(category.getId()));
    }
}
