package org.aston.homeworkThree.service;

import org.aston.homeworkThree.dao.ContactDAO;
import org.aston.homeworkThree.model.Contact;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class ContactDAOImp implements ContactDAO {
    private final SessionFactory sessionFactory;

    public ContactDAOImp(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void saveContact(Contact contact) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (contact == null) {
                    transaction.rollback();
                    throw new IllegalArgumentException("Contact не может быть null.");
                }
                session.persist(contact);
                transaction.commit();
            } catch (Exception ex) {
                rollbackIfActive(transaction);
                throw new RuntimeException("Ошибка добавления контакта в БД: ", ex);
            }
        }
    }

    @Override
    public Optional<Contact> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            Contact contact = session.get(Contact.class, id);
            return Optional.ofNullable(contact);
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка при поиске пользователя по id = " + id + ": ", ex);
        }
    }

    @Override
    public List<Contact> findAllContactByUserId(long userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT c FROM Contact c WHERE c.user.id = :userId", Contact.class)
                    .setParameter("userId", userId)
                    .list();
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка получения списка контактов по id пользователя " + userId + ": ", ex);
        }

    }

    @Override
    public Optional<Contact> updateContact(long id, Contact contact) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Contact currentContact = session.get(Contact.class, id);
                if (currentContact == null || contact == null) {
                    transaction.rollback();
                    return Optional.empty();
                }
                currentContact.setContactName(contact.getContactName());
                currentContact.setNumber(contact.getNumber());
                currentContact.setCategoryContact(contact.getCategoryContact());
                transaction.commit();
                return Optional.of(currentContact);
            } catch (Exception ex) {
                rollbackIfActive(transaction);
                throw new RuntimeException("Ошибка при обновлении данных контакта: ", ex);
            }
        }
    }

    @Override
    public boolean deleteContact(long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Contact contact = session.get(Contact.class, id);
                if (contact == null) {
                    transaction.rollback();
                    return false;
                }
                session.remove(contact);
                transaction.commit();
                return true;
            } catch (Exception ex) {
                rollbackIfActive(transaction);
                throw new RuntimeException("Ошибка при удалении контакта по id = " + id + ": ", ex);
            }
        }
    }

    private void rollbackIfActive(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }
}
