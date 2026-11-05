package org.aston.homeworkTwo;

import org.aston.homeworkTwo.consolInterface.ConsoleInteface;
import org.aston.homeworkTwo.service.UserService;
import org.aston.homeworkTwo.util.HibernateSessionFactoryUtil;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("Консольное приложение для работы с пользователями.");
        try {
            ConsoleInteface consoleInteface = new ConsoleInteface(new UserService());
            consoleInteface.run();
        } finally {
            HibernateSessionFactoryUtil.shutdown();
        }

    }
}
