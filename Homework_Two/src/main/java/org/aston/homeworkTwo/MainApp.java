package org.aston.homeworkTwo;

import org.aston.homeworkTwo.consolInterface.ConsoleInterface;
import org.aston.homeworkTwo.service.UserService;
import org.aston.homeworkTwo.util.HibernateSessionFactoryUtil;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("Консольное приложение для работы с пользователями.");
        try {
            ConsoleInterface consoleInterface = new ConsoleInterface(new UserService());
            consoleInterface.run();
        } finally {
            HibernateSessionFactoryUtil.shutdown();
        }
    }
}
