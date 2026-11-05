package org.aston.homeworkTwo.consolInterface;

import org.aston.homeworkTwo.dao.UserDAO;
import org.aston.homeworkTwo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Optional;
import java.util.Scanner;
import java.util.List;

public class ConsoleInteface {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleInteface.class);
    private final UserDAO userDAO;

    public ConsoleInteface(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void run() {
        while (true) {
            printMenu();
            String choice = SCANNER.nextLine().trim();
            switch (choice) {
                case "1" -> saveUserConsoleInterface();
                case "2" -> updateUserConsoleInterface();
                case "3" -> findByIdConsoleInterface();
                case "4" -> findAllConsoleInterface();
                case "5" -> removeUserConsoleInterface();
                case "6" -> {
                    return;
                }
                default -> System.out.println("Неверный пункт меню.");
            }
        }
    }

    private void printMenu() {
        System.out.println("Выберете пункт из меню для продолжения работы: ");
        System.out.println("""
                    1. Добавить пользователя.
                    2. Изменить данные о пользователе.
                    3. Найти пользователя по id.
                    4. Вывести всех пользователей.
                    5. Удалить пользователя.
                    6. Завершить работу программы.
                    """);
    }

    private void saveUserConsoleInterface() {
        System.out.println("Введите данные о пользователе.");

        System.out.print("Имя пользователя: ");
        String name = SCANNER.nextLine().trim();

        System.out.print("Email пользователя: ");
        String email = SCANNER.nextLine().trim();

        System.out.print("Возраст пользователя: ");
        String ageString = SCANNER.nextLine().trim();

        if (name.isBlank() || email.isBlank()) {
            LOGGER.warn("Попытка сохранить пользователя с пустым именем или email.");
            System.out.println("Имя и email не могут быть пустыми. Возврат в главное меню.");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageString);
            User user = new User(name, email, age);
            User userSave = userDAO.saveUser(user);
            LOGGER.info("Создан пользователь: user={}", userSave);
            System.out.println("Пользователь успешно добавлен: " + userSave);
        } catch (NumberFormatException ex) {
            LOGGER.warn("Некорректный возраст при сохранении: {}", ageString);
            System.out.println("Некорректное значение возраста. Возврат в главное меню.");
        } catch (RuntimeException ex) {
            LOGGER.error("Ошибка при сохранении пользователя.", ex);
            System.out.println("Не удалось добавить пользователя: " + ex.getMessage());
        }
    }

    private void updateUserConsoleInterface() {
        System.out.println("Введите данные для изменения. Если изменения вносить не нужно, то оставьте поле пустым.");

        System.out.print("Id пользователя, информацию о котором необходимо изменить: ");
        String idString = SCANNER.nextLine().trim();

        long id = getParseLong(idString);
        if (id == -1) {
            return;
        }

        System.out.print("Новое имя пользователя: ");
        String name = SCANNER.nextLine().trim();

        System.out.print("Новый email пользователя: ");
        String email = SCANNER.nextLine().trim();

        System.out.print("Новый возраст пользователя: ");
        String ageString = SCANNER.nextLine().trim();

        try {
            Optional<User> existing = userDAO.findById(id);
            if (existing.isEmpty()) {
                LOGGER.warn("Попытка найти пользователя с несуществующим id. id={}", id);
                System.out.println("Пользователь с id = " + id + " не найден.");
                return;
            }
            User current = existing.get();

            String newName = name.isBlank() ? current.getName() : name;
            String newEmail = email.isBlank() ? current.getEmail() : email;
            int newAge = ageString.isBlank() ? current.getAge() : Integer.parseInt(ageString);

            User updateUser = new User(newName, newEmail, newAge);
            Optional<User> updated = userDAO.updateUser(id, updateUser);
            if (updated.isEmpty()) {
                LOGGER.warn("Попытка найти пользователя с несуществующим id. id={}", id);
                System.out.println("Пользователь с id = " + id + " не найден.");
                return;
            }
            LOGGER.info("Успешное изменение информации о пользователе. user={}", updated.get());
            System.out.println("Информация о пользователе успешно изменена: " + updated.get());
        } catch (NumberFormatException ex) {
            LOGGER.warn("Попытка изменения данных о возрасте неуспешна, некорректное значение поля age.");
            System.out.println("Некорректное значение возраста. Возврат в главное меню.");
        } catch (RuntimeException ex) {
            LOGGER.error("Ошибка при попытке изменить данные о пользователе.", ex);
            System.out.println("Не удалось изменить информацию о пользователе: " + ex.getMessage());
        }
    }

    private void findByIdConsoleInterface() {
        System.out.print("Введите id пользователя, которого хотите найти: ");
        String idString = SCANNER.nextLine().trim();

        long id = getParseLong(idString);
        if (id == -1) {
            return;
        }

        try {
            Optional<User> user = userDAO.findById(id);
            if (user.isEmpty()) {
                LOGGER.warn("Попытка найти пользователя с несуществующим id. id={}", id);
                System.out.println("Пользователь с id = " + id + " не найден.");
                return;
            }
            System.out.println("Найден пользователь: " + user.get());
        } catch (RuntimeException ex) {
            System.out.println("Не удалось найти пользователя. " + ex.getMessage());
        }
    }

    private void findAllConsoleInterface() {
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
            System.out.println("Не удалось получить список пользователей: " + ex.getMessage());
        }
    }

    private void removeUserConsoleInterface() {
        System.out.print("Введите id пользователя, которого хотите удалить: ");
        String idString = SCANNER.nextLine().trim();

        long id = getParseLong(idString);
        if (id == -1) {
            return;
        }

        try {
            if (userDAO.deleteUser(id)) {
                System.out.println("Пользователь успешно удален.");
            } else {
                System.out.println("Пользователь с id = " + id + " не найден.");
            }
        } catch (RuntimeException ex) {
            System.out.println("Не удалось удалить пользователя: " + ex.getMessage());
        }
    }

    private long getParseLong(String idString) {
        try {
            return Long.parseLong(idString);
        } catch (NumberFormatException ex) {
            LOGGER.warn("Введено некорректное значение id={}.", idString);
            System.out.println("Некорректное значение id. Возврат в главное меню.");
            return -1;
        }
    }
}
