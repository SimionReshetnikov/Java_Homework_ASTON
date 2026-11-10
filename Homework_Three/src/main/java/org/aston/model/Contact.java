package org.aston.model;

import jakarta.persistence.*;

@Entity
@Table(name = "contact")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "contact_name", nullable = false, length = 50)
    private String contactName;

    @Column(name = "number", nullable = false, unique = true, length = 15)
    private String number;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany
    @JoinColumn(name = "category_contact_id")
    private CategoryContact categoryContact;

    public Contact() {}

    public Contact(String contactName, String number) {
        this.contactName = contactName;
        this.number = number;
    }

    public long getId() {
        return id;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CategoryContact getCategoryContact() {
        return categoryContact;
    }

    public void setCategoryContact(CategoryContact categoryContact) {
        this.categoryContact = categoryContact;
    }
}
