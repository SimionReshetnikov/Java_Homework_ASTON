package org.aston.homeworkTwo.consolInterface;

import org.aston.homeworkTwo.dao.UserDAO;
import org.aston.homeworkTwo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Optional;
import java.util.Scanner;
import java.util.List;

public class ConsoleInterface {
    private final Scanner scanner;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleInterface.class);
    private final UserDAO userDAO;

    public ConsoleInterface(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.scanner = new Scanner(System.in);
    }

    private ConsoleInterface(UserDAO userDAO, Scanner scanner) {
        this.userDAO = userDAO;
        this.scanner = scanner;
    }

    public void run() {
        while (true) {
            showMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> handleSaveUser();
                case "2" -> handleUpdateUser();
                case "3" -> handleFindById();
                case "4" -> handleFindAll();
                case "5" -> handleRemoveUser();
                case "6" -> {
                    return;
                }
                default -> {
                    LOGGER.debug("Неверный пункт меню: '{}'", choice);
                    System.out.println("Неверный пункт меню.");
                }
            }
        }
    }

    private void showMenu() {
        System.out.println("Выберите пункт из меню для продолжения работы: ");
        System.out.println("""
                    1. Добавить пользователя.
                    2. Изменить данные о пользователе.
                    3. Найти пользователя по id.
                    4. Вывести всех пользователей.
                    5. Удалить пользователя.
                    6. Завершить работу программы.
                    """);
    }

    private void handleSaveUser() {
        System.out.println("Введите данные о пользователе.");

        System.out.print("Имя пользователя: ");
        String name = scanner.nextLine().trim();

        System.out.print("Email пользователя: ");
        String email = scanner.nextLine().trim();

        System.out.print("Возраст пользователя: ");
        String ageString = scanner.nextLine().trim();

        if (name.isBlank() || email.isBlank()) {
            LOGGER.warn("Попытка сохранить пользователя с пустым именем или email.");
            System.out.println("Имя и email не могут быть пустыми. Возврат в главное меню.");
            return;
        }

        Optional<Integer> ageOpt = parseAndValidateAge(ageString);
        if (ageOpt.isEmpty()) {
            return;
        }
        int age = ageOpt.get();

        try {
            User user = new User(name, email, age);
            User userSave = userDAO.saveUser(user);
            LOGGER.info("Создан пользователь: {}", userSave);
            System.out.println("Пользователь успешно добавлен: " + userSave);
        } catch (RuntimeException ex) {
            LOGGER.error("Ошибка при сохранении пользователя.", ex);
            System.out.println("Не удалось добавить пользователя: " + ex.getMessage());
        }
    }

    private void handleUpdateUser() {
        System.out.println("Введите данные для изменения. Если изменения вносить не нужно, то оставьте поле пустым.");

        System.out.print("Id пользователя, информацию о котором необходимо изменить: ");
        String idString = scanner.nextLine().trim();

        Optional<Long> idOpt = parseId(idString);
        if (idOpt.isEmpty()) {
            return;
        }
        long id = idOpt.get();

        Optional<User> existing;
        try {
            existing = userDAO.findById(id);
        } catch (RuntimeException ex) {
            LOGGER.error("Ошибка при поиске пользователя id = {} перед обновлением.", id, ex);
            System.out.println("Не удалось получить данные о пользователе: " + ex.getMessage());
            return;
        }

        if (existing.isEmpty()) {
            printUserNotFound(id);
            return;
        }

        User current = existing.get();

        System.out.print("Новое имя пользователя: ");
        String name = scanner.nextLine().trim();

        System.out.print("Новый email пользователя: ");
        String email = scanner.nextLine().trim();

        System.out.print("Новый возраст пользователя: ");
        String ageString = scanner.nextLine().trim();

        int newAge;
        if (ageString.isBlank()) {
            newAge = current.getAge();
        } else {
            Optional<Integer> ageOpt = parseAndValidateAge(ageString);
            if (ageOpt.isEmpty()) {
                return;
            }
            newAge = ageOpt.get();
        }

        String newName = name.isBlank() ? current.getName() : name;
        String newEmail = email.isBlank() ? current.getEmail() : email;

        User updateUser = new User(newName, newEmail, newAge);

        try {
            Optional<User> updated = userDAO.updateUser(id, updateUser);
            if (updated.isEmpty()) {
                printUserNotFound(id);
                return;
            }
            LOGGER.info("Обновлен пользователь: id = {}.", id);
            System.out.println("Информация о пользователе успешно изменена: " + updated.get());
        } catch (RuntimeException ex) {
            LOGGER.error("Ошибка при попытке изменить данные о пользователе.", ex);
            System.out.println("Не удалось изменить информацию о пользователе: " + ex.getMessage());
        }
    }

    private void handleFindById() {
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
            LOGGER.info("Найден пользователь: {}", user);
            System.out.println("Найден пользователь: " + user);
        } catch (RuntimeException ex) {
            LOGGER.error("Ошибка при поиске пользователя.", ex);
            System.out.println("Не удалось найти пользователя. " + ex.getMessage());
        }
    }

    private void handleFindAll() {
        try {
            List<User> userList = userDAO.findAll();
            if (userList.isEmpty()) {
                LOGGER.warn("Список пользователей пуст.");
                System.out.println("Список пользователей пуст.");
                return;
            }
            System.out.println("Список пользователей: ");
            userList.sort(Comparator.comparing(User::getId));
            for (User user : userList) {
                System.out.println(user);
            }
            LOGGER.info("Список пользователей выведен успешно.");
        } catch (RuntimeException ex) {
            LOGGER.error("Ошибка при получении списка пользователей.", ex);
            System.out.println("Не удалось получить список пользователей. Ошибка: " + ex.getMessage());
        }
    }

    private void handleRemoveUser() {
        System.out.print("Введите id пользователя, которого хотите удалить: ");
        String idString = scanner.nextLine().trim();

        Optional<Long> idOpt = parseId(idString);
        if (idOpt.isEmpty()) {
            return;
        }
        long id = idOpt.get();

        try {
            if (userDAO.deleteUser(id)) {
                LOGGER.info("Успешное удаление пользователя.");
                System.out.println("Пользователь успешно удален.");
            } else {
                printUserNotFound(id);
            }
        } catch (RuntimeException ex) {
            LOGGER.error("Ошибка при удалении пользователя.", ex);
            System.out.println("Не удалось удалить пользователя: " + ex.getMessage());
        }
    }

    private Optional<Long> parseId(String idString) {
        try {
            return Optional.of(Long.parseLong(idString));
        } catch (NumberFormatException ex) {
            LOGGER.warn("Введено некорректное значение id = {}.", idString);
            System.out.println("Некорректное значение id. Возврат в главное меню.");
            return Optional.empty();
        }
    }

    private Optional<Integer> parseAndValidateAge(String ageString) {
        int age;
        try {
            age = Integer.parseInt(ageString);
        } catch (NumberFormatException ex) {
            LOGGER.warn("Некорректный возраст: {}", ageString);
            System.out.println("Возраст должен быть целым числом. Возврат в главное меню.");
            return Optional.empty();
        }

        if (age <= 0 || age > 120) {
            LOGGER.warn("Возраст вне допустимого диапазона: {}", age);
            System.out.println("Возраст должен быть от 1 до 120. Возврат в главное меню.");
            return Optional.empty();
        }

        return Optional.of(age);
    }

    private void printUserNotFound(long id) {
        LOGGER.warn("Пользователь с id = {} не найден.", id);
        System.out.println("Пользователь с id = " + id + " не найден.");
    }
}
