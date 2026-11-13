package org.aston.homeworkThree.dao;

import org.aston.homeworkThree.model.Contact;

import java.util.List;
import java.util.Optional;

public interface ContactDAO {
    void saveContact(Contact contact);
    Optional<Contact> findById(long id);
    List<Contact> findAllContactByUserId(long userId);
    Optional<Contact> updateContact(long id, Contact contact);
    boolean deleteContact(long id);
}
