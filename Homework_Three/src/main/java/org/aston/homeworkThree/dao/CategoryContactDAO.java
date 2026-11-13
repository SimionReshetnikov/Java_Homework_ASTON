package org.aston.homeworkThree.dao;

import org.aston.homeworkThree.model.CategoryContact;

import java.util.List;
import java.util.Optional;

public interface CategoryContactDAO {
    void saveCategory(CategoryContact categoryContact);
    Optional<CategoryContact> updateCategory(long id, CategoryContact categoryContact);
    Optional<CategoryContact> findById(long id);
    List<CategoryContact> findAll();
    boolean deleteCategory(long id);
}
