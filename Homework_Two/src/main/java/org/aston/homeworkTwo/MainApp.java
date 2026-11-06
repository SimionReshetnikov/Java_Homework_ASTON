package org.aston.homeworkTwo;

import org.aston.homeworkTwo.consolInterface.ConsoleInterface;
import org.aston.homeworkTwo.service.UserService;
import org.aston.homeworkTwo.util.HibernateSessionFactoryUtil;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("Консольное приложение для работы с пользователями.");
        try {
            ConsoleInterface consoleInteface = new ConsoleInterface(new UserService());
            consoleInteface.run();
        } finally {
            HibernateSessionFactoryUtil.shutdown();
        }

    }
}
