package org.example.HomeworkFour.repository;

import org.example.HomeworkFour.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId")
    List<Contact> findAllContactByUserId(@Param("userId") long id);
}
