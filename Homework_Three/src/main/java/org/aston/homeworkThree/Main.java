package org.aston.homeworkThree;

import org.aston.homeworkThree.dao.CategoryContactDAO;
import org.aston.homeworkThree.dao.ContactDAO;
import org.aston.homeworkThree.dao.UserDAO;
import org.aston.homeworkThree.service.CategoryContactDAOImp;
import org.aston.homeworkThree.service.ContactDAOImp;
import org.aston.homeworkThree.service.UserDAOImp;
import org.aston.homeworkThree.userInterface.Application;
import org.aston.homeworkThree.userInterface.CategoryContactUI;
import org.aston.homeworkThree.userInterface.ContactUI;
import org.aston.homeworkThree.userInterface.UserUI;
import org.aston.homeworkThree.util.HibernateSessionFactoryUtil;
import org.hibernate.SessionFactory;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
        Scanner scanner = new Scanner(System.in);

        UserDAO userDAO = new UserDAOImp(sessionFactory);
        ContactDAO contactDAO = new ContactDAOImp(sessionFactory);
        CategoryContactDAO categoryContactDAO = new CategoryContactDAOImp(sessionFactory);

        UserUI userUI = new UserUI(userDAO, scanner);
        ContactUI contactUI = new ContactUI(contactDAO, scanner);
        CategoryContactUI categoryContactUI = new CategoryContactUI(categoryContactDAO, scanner);

        Application application = new Application(userUI, contactUI, categoryContactUI, scanner);
        application.run();
    }
}