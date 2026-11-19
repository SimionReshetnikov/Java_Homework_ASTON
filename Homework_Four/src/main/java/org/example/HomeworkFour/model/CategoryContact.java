package org.example.HomeworkFour.model;

import jakarta.persistence.*;

@Entity
@Table(name = "category_contact")
public class CategoryContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "category_name", nullable = false, unique = true, length = 50)
    private String categoryName;

    public CategoryContact() {}

    public CategoryContact(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
