package org.aston.homeworkThree.service;

import org.aston.homeworkThree.dao.CategoryContactDAO;
import org.aston.homeworkThree.model.CategoryContact;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class CategoryContactDAOImp implements CategoryContactDAO {
    private final SessionFactory sessionFactory;

    public CategoryContactDAOImp(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void saveCategory(CategoryContact categoryContact) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(categoryContact);
                transaction.commit();
            } catch (Exception ex) {
                rollbackIfActive(transaction);
                throw new RuntimeException("Ошибка при добавлении категории контакта: ", ex);
            }
        }
    }

    @Override
    public Optional<CategoryContact> updateCategory(long id, CategoryContact categoryContact) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                CategoryContact currentContact = session.get(CategoryContact.class, id);
                if (currentContact == null || categoryContact == null) {
                    transaction.rollback();
                    return Optional.empty();
                }

                Long duplicateCount = session.createQuery(
                                "SELECT COUNT(c) FROM CategoryContact c WHERE c.categoryName = :name AND c.id != :id", Long.class)
                        .setParameter("name", categoryContact.getCategoryName())
                        .setParameter("id", id)
                        .getSingleResult();

                if (duplicateCount > 0) {
                    transaction.rollback();
                    throw new IllegalStateException("Категория с именем "
                            + categoryContact.getCategoryName() + " уже существует.");
                }

                currentContact.setCategoryName(categoryContact.getCategoryName());
                transaction.commit();
                return Optional.of(currentContact);
            } catch (IllegalStateException ex) {
                rollbackIfActive(transaction);
                throw ex;
            } catch (Exception ex) {
                rollbackIfActive(transaction);
                throw new RuntimeException("Ошибка изменения данных категории контакта: ", ex);
            }
        }
    }

    @Override
    public Optional<CategoryContact> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            CategoryContact categoryContact = session.get(CategoryContact.class, id);
            return Optional.ofNullable(categoryContact);
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка получения данных о категории контакта: ", ex);
        }
    }

    @Override
    public List<CategoryContact> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM CategoryContact", CategoryContact.class).list();
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка получения списка категории контактов: ", ex);
        }
    }

    @Override
    public boolean deleteCategory(long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                CategoryContact categoryContact = session.get(CategoryContact.class, id);
                if (categoryContact == null) {
                    transaction.rollback();
                    return false;
                }

                Long count = session.createQuery(
                                "SELECT COUNT(c) FROM Contact c WHERE c.categoryContact.id = :categoryId", Long.class)
                        .setParameter("categoryId", id)
                        .getSingleResult();

                if (count > 0) {
                    transaction.rollback();
                    throw new IllegalStateException("Невозможно удалить категорию. К ней привязано " + count + " контактов.");
                }

                session.remove(categoryContact);
                transaction.commit();
                return true;
            } catch (IllegalStateException ex) {
                rollbackIfActive(transaction);
                throw ex;
            } catch (Exception ex) {
                rollbackIfActive(transaction);
                throw new RuntimeException("Ошибка при удалении категории контактов: " + ex);
            }
        }
    }

    private void rollbackIfActive(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }
}
