package org.aston.homeworkThree.userInterface;

import org.aston.homeworkThree.dao.CategoryContactDAO;
import org.aston.homeworkThree.model.CategoryContact;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CategoryContactUI {
    private final CategoryContactDAO categoryContactDAO;
    private final Scanner scanner;

    public CategoryContactUI(CategoryContactDAO categoryContactDAO, Scanner scanner) {
        this.categoryContactDAO = categoryContactDAO;
        this.scanner = scanner;
    }

    public void handleSaveCategoryContact() {
        System.out.println("Введите название категории, которую хотите создать: ");
        String category = scanner.nextLine().trim();

        if (category.isBlank()) {
            System.out.println("Наименование категории контактов не может быть пустым. Возврат в главное меню.");
            return;
        }

        CategoryContact categoryContact = new CategoryContact(category);
        try {
            categoryContactDAO.saveCategory(categoryContact);
            System.out.println("Категория контактов успешно добавлена.");
        } catch (RuntimeException ex) {
            System.out.println("Не удалось добавить категорию контактов: " + ex.getMessage());
        }
    }

    public void handleUpdateCategoryContact() {
        System.out.println("Введите id категории, которую хотите изменить: ");
        Optional<Long> categoryIdOpt = parseId(scanner.nextLine().trim());

        if (categoryIdOpt.isEmpty()) {
            return;
        }

        long id = categoryIdOpt.get();
        Optional<CategoryContact> currentCategory;
        try {
            currentCategory = categoryContactDAO.findById(id);
        } catch (RuntimeException ex) {
            System.out.println("Ошибка при попытке найти категорию для изменения данных: " + ex.getMessage());
            return;
        }

        if (currentCategory.isEmpty()) {
            printContactNotFound(id);
            return;
        }

        System.out.println("Введите данные для изменения. Если изменения вносить не нужно, то оставьте поле пустым.");
        System.out.println("Новое название категории: ");
        String updateCategoryName = scanner.nextLine().trim();

        String newCategoryName = updateCategoryName.isBlank() ? currentCategory.get().getCategoryName() : updateCategoryName;
        CategoryContact updateCategory = new CategoryContact(newCategoryName);

        try {
            Optional<CategoryContact> updated = categoryContactDAO.updateCategory(id, updateCategory);
            if (updated.isEmpty()) {
                System.out.println("Не удалось изменить категорию. Возврат в главное меню.");
                return;
            }
            System.out.println("Категория успешно изменена.");
        } catch (RuntimeException ex) {
            System.out.println("Не удалось обновить категорию. " + ex.getMessage());
        }
    }

    public void handleFindByIdCategoryContact() {
        System.out.println("Введите id категории, которую хотите найти: ");
        Optional<Long> categoryIdOpt = parseId(scanner.nextLine().trim());
        if (categoryIdOpt.isEmpty()) {
            return;
        }
        long id = categoryIdOpt.get();

        try {
            Optional<CategoryContact> categoryOpt = categoryContactDAO.findById(id);
            if (categoryOpt.isEmpty()) {
                printContactNotFound(id);
                return;
            }
            System.out.println("Найдена категория контактов: " + categoryOpt.get());
        } catch (RuntimeException ex) {
            System.out.println("Не удалось найти категорию. " + ex.getMessage());
        }
    }

    public void handleFindAllCategoryContact() {
        try {
            List<CategoryContact> contactList = categoryContactDAO.findAll();
            if (contactList.isEmpty()) {
                System.out.println("Список категорий контактов пуст.");
                return;
            }
            System.out.println("Список категорий контактов: ");
            contactList.sort(Comparator.comparing(CategoryContact::getId));
            for (CategoryContact category : contactList) {
                System.out.println(category);
            }
        } catch (RuntimeException ex) {
            System.out.println("Не удалось получить список категорий контактов. Ошибка: " + ex.getMessage());
        }
    }

    public void handleRemoveCategoryContact() {
        System.out.println("Предупреждение: удалить можно только категорию, к которой не привязаны контакты.");
        System.out.print("Введите id категории, которую хотите удалить: ");
        String idString = scanner.nextLine().trim();

        Optional<Long> idOpt = parseId(idString);
        if (idOpt.isEmpty()) {
            return;
        }
        long id = idOpt.get();

        try {
            if (categoryContactDAO.deleteCategory(id)) {
                System.out.println("Категория контактов успешно удалена.");
            } else {
                printContactNotFound(id);
            }
        } catch (RuntimeException ex) {
            System.out.println("Не удалось удалить категорию контактов: " + ex.getMessage());
        }
    }

    private void printContactNotFound(long id) {
        System.out.println("Категория с id = " + id + " не найдена.");
    }

    private Optional<Long> parseId(String idString) {
        try {
            return Optional.of(Long.parseLong(idString));
        } catch (NumberFormatException ex) {
            System.out.println("Некорректное значение id. Возврат в главное меню.");
            return Optional.empty();
        }
    }
}
