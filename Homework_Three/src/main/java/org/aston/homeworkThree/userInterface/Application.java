package org.aston.homeworkThree.userInterface;

import java.util.Scanner;

public class Application {
    private UserUI userUI;
    private ContactUI contactUI;
    private CategoryContactUI categoryContactUI;
    private Scanner scanner;

    public Application(UserUI userUI, ContactUI contactUI, CategoryContactUI categoryContactUI, Scanner scanner) {
        this.userUI = userUI;
        this.contactUI = contactUI;
        this.categoryContactUI = categoryContactUI;
        this.scanner = scanner;
    }

    public void run() {
        while (true) {
            mainMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> showUserMenu();
                case "2" -> showContactMenu();
                case "3" -> showCategoryMenu();
                case "0" -> {
                    System.out.println("Окончание работы приложения.");
                    return;
                }
                default -> System.out.println("Некорректный пункт меню. Попробуйте снова.");
            }
        }
    }

    private void mainMenu() {
        System.out.println("""
                1. Управление пользователями.
                2. Управление контактами.
                3. Управление категориями контактов.
                0. Выход
                """);
        System.out.println("Выберете пункт меню: ");
    }

    private void showUserMenu() {
        while (true) {
            System.out.println("""
                    1. Создать пользователя.
                    2. Найти по ID.
                    3. Показать всех пользователей.
                    4. Обновить данные пользователя.
                    5. Удалить пользователя.
                    0. Назад.
                    """);
            System.out.println("Выберете действие.");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> userUI.handleSaveUser();
                case "2" -> userUI.handleFindById();
                case "3" -> userUI.handleFindAll();
                case "4" -> userUI.handleUpdateUser();
                case "5" -> userUI.handleRemoveUser();
                case "0" -> { return; }
                default -> System.out.println("Некорректный пункт меню. Попробуйте снова.");
            }
        }
    }

    private void showContactMenu() {
        while (true) {
            System.out.println("""
                    1. Создать контакт.
                    2. Найти по ID.
                    3. Показать все контакты одного пользователя.
                    4. Обновить данные контакта.
                    5. Удалить контакт.
                    0. Назад.
                    """);
            System.out.println("Выберете действие.");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> contactUI.handleSaveContact();
                case "2" -> contactUI.handleFindByIdContact();
                case "3" -> contactUI.handleFindAllContactByUserId();
                case "4" -> contactUI.handleUpdateContact();
                case "5" -> contactUI.handleDeleteContact();
                case "0" -> { return; }
                default -> System.out.println("Некорректный пункт меню. Попробуйте снова.");
            }
        }
    }

    private void showCategoryMenu() {
        while (true) {
            System.out.println("""
                    1. Создать новую категорию.
                    2. Найти по ID.
                    3. Показать все категории контактов.
                    4. Обновить данные категории.
                    5. Удалить категорию.
                    0. Назад.
                    """);
            System.out.println("Выберете действие.");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> categoryContactUI.handleSaveCategoryContact();
                case "2" -> categoryContactUI.handleFindByIdCategoryContact();
                case "3" -> categoryContactUI.handleFindAllCategoryContact();
                case "4" -> categoryContactUI.handleUpdateCategoryContact();
                case "5" -> categoryContactUI.handleRemoveCategoryContact();
                case "0" -> { return; }
                default -> System.out.println("Некорректный пункт меню. Попробуйте снова.");
            }
        }
    }
}
