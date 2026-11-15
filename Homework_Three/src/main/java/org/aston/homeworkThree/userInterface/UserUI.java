package org.aston.homeworkThree.userInterface;

import org.aston.homeworkThree.dao.UserDAO;
import org.aston.homeworkThree.model.User;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserUI {
    private final UserDAO userDAO;
    private final Scanner scanner;

    public UserUI(UserDAO userDAO, Scanner scanner) {
        this.userDAO = userDAO;
        this.scanner = scanner;
    }

    public void handleSaveUser() {
        System.out.println("Введите данные о пользователе.");

        System.out.println("Введите имя: ");
        String firstName = scanner.nextLine().trim();

        System.out.println("Введите фамилию: ");
        String lastName = scanner.nextLine().trim();

        if (firstName.isBlank() || lastName.isBlank()) {
            System.out.println("Имя или фамилия не могут быть пустыми. Возврат в главное меню.");
            return;
        }

        try {
            User user = new User(firstName, lastName);
            userDAO.saveUser(user);
            System.out.println("Пользователь успешно добавлен.");
        } catch (RuntimeException ex) {
            System.out.println("Не удалось добавить пользователя: " + ex.getMessage());
        }
    }

    public void handleUpdateUser() {
        System.out.println("Введите id пользователя, данные которого хотите изменить: ");

        String userId = scanner.nextLine().trim();
        Optional<Long> idOpt = parseId(userId);

        if (idOpt.isEmpty()) {
            return;
        }

        long id = idOpt.get();

        Optional<User> currentUserOpt;
        try {
            currentUserOpt = userDAO.findById(id);
        } catch (RuntimeException ex) {
            System.out.println("Не удалось получить данные о пользователе: " + ex.getMessage());
            return;
        }

        if (currentUserOpt.isEmpty()) {
            printUserNotFound(id);
            return;
        }

        User currentUser = currentUserOpt.get();

        System.out.println("Введите данные для изменения. Если изменения вносить не нужно, то оставьте поле пустым.");
        System.out.println("Введите новое имя: ");
        String updateFirstName = scanner.nextLine().trim();
        System.out.println("Введите новую фамилию: ");
        String updateLastName = scanner.nextLine().trim();

        String newFirstName = updateFirstName.isBlank() ? currentUser.getFirstName() : updateFirstName;
        String newLastName = updateLastName.isBlank() ? currentUser.getLastName() : updateLastName;
        User updateUser = new User(newFirstName, newLastName);

        try {
            Optional<User> updated = userDAO.updateUser(id, updateUser);
            if (updated.isEmpty()) {
                printUserNotFound(id);
                return;
            }
            System.out.println("Информация о пользователе успешно изменена: " + updated.get());
        } catch (RuntimeException ex) {
            System.out.println("Не удалось изменить информацию о пользователе: " + ex.getMessage());
        }
    }

    public void handleFindById() {
        System.out.print("Введите id пользователя, которого хотите найти: ");
        String idString = scanner.nextLine().trim();

        Optional<Long> idOpt = parseId(idString);
        if (idOpt.isEmpty()) {
            return;
        }
        long id = idOpt.get();

        try {
            Optional<User> userOpt = userDAO.findById(id);
            if (userOpt.isEmpty()) {
                printUserNotFound(id);
                return;
            }
            User user = userOpt.get();
            System.out.println("Найден пользователь: " + user);
        } catch (RuntimeException ex) {
            System.out.println("Не удалось найти пользователя. " + ex.getMessage());
        }
    }

    public void handleFindAll() {
        try {
            List<User> userList = userDAO.findAll();
            if (userList.isEmpty()) {
                System.out.println("Список пользователей пуст.");
                return;
            }
            System.out.println("Список пользователей: ");
            userList.sort(Comparator.comparing(User::getId));
            for (User user : userList) {
                System.out.println(user);
            }
        } catch (RuntimeException ex) {
            System.out.println("Не удалось получить список пользователей. Ошибка: " + ex.getMessage());
        }
    }

    public void handleRemoveUser() {
        System.out.print("Введите id пользователя, которого хотите удалить: ");
        String idString = scanner.nextLine().trim();

        Optional<Long> idOpt = parseId(idString);
        if (idOpt.isEmpty()) {
            return;
        }
        long id = idOpt.get();

        try {
            if (userDAO.removeUser(id)) {
                System.out.println("Пользователь успешно удален.");
            } else {
                printUserNotFound(id);
            }
        } catch (RuntimeException ex) {
            System.out.println("Не удалось удалить пользователя: " + ex.getMessage());
        }
    }

    private Optional<Long> parseId(String idString) {
        try {
            return Optional.of(Long.parseLong(idString));
        } catch (NumberFormatException ex) {
            System.out.println("Некорректное значение id. Возврат в главное меню.");
            return Optional.empty();
        }
    }

    private void printUserNotFound(long id) {
        System.out.println("Пользователь с id = " + id + " не найден.");
    }
}
