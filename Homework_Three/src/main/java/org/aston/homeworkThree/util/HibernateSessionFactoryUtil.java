package org.aston.homeworkThree.util;

import org.aston.homeworkThree.model.CategoryContact;
import org.aston.homeworkThree.model.Contact;
import org.aston.homeworkThree.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactoryUtil implements AutoCloseable {
    private static volatile SessionFactory sessionFactory;

    public static SessionFactory initSessionFactory(String nameConfig) {
        if (sessionFactory == null) {
            synchronized (HibernateSessionFactoryUtil.class) {
                try {
                    Configuration configuration = new Configuration().configure(nameConfig);
                    configuration.addAnnotatedClass(User.class);
                    configuration.addAnnotatedClass(Contact.class);
                    configuration.addAnnotatedClass(CategoryContact.class);
                    StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                            .applySettings(configuration.getProperties());
                    sessionFactory = configuration.buildSessionFactory(builder.build());
                } catch (Exception ex) {
                    throw new RuntimeException("Не удалось создать SessionFactory.", ex);
                }
            }
        }
        return sessionFactory;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException("SessionFactory не создана. Сначала нужно вызвать метод initSessionFactory.");
        }
        return sessionFactory;
    }


    @Override
    public void close() throws Exception {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}

