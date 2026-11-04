package org.aston.homeworkTwo;

import org.aston.homeworkTwo.consolInterface.ConsolInteface;
import org.aston.homeworkTwo.service.UserService;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("Консольное приложение для работы с пользователями.");
        ConsolInteface consolInteface = new ConsolInteface(new UserService());
        consolInteface.run();
    }
}
