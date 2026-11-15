package org.aston.homeworkThree.userInterface;

import org.aston.homeworkThree.dao.CategoryContactDAO;
import org.aston.homeworkThree.dao.ContactDAO;
import org.aston.homeworkThree.dao.UserDAO;
import org.aston.homeworkThree.model.Contact;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ContactUI {
    private final ContactDAO contactDAO;
    private final Scanner scanner;

    public ContactUI(ContactDAO contactDAO, Scanner scanner) {
        this.contactDAO = contactDAO;
        this.scanner = scanner;
    }

    protected void handleSaveContact() {
        System.out.println("Введите данные о контакте: ");

        System.out.println("Введите имя контакта: ");
        String contactName = scanner.nextLine().trim();
        System.out.println("Введите номер телефона: ");
        String phoneNumber = scanner.nextLine().trim();
        System.out.println("Введите id пользователя, к которому относится контакт: ");
        String userIdString = scanner.nextLine().trim();
        System.out.println("Введите id категории контакта к которой относится контакт: ");
        String categoryIdString = scanner.nextLine().trim();

        if (contactName.isBlank() || phoneNumber.isBlank()) {
            System.out.println("Имя или номер телефона контакта не могут быть пустыми. Возврат в главное меню.");
            return;
        }

        Optional<Long> userIdOpt = parseId(userIdString);
        if (userIdOpt.isEmpty()) {
            System.out.println("Контакт должен быть привязан к пользователю. Возврат в главное меню.");
            return;
        }

        Optional<Long> categoryIdOpt = parseId(categoryIdString);
        if (categoryIdOpt.isEmpty()) {
            System.out.println("Контакт должен быть привязан к категории. Возврат в главное меню.");
            return;
        }

        Contact contact = new Contact(contactName, phoneNumber);

        try {
            contactDAO.saveContact(contact, userIdOpt.get(), categoryIdOpt.get());
            System.out.println("Контакт успешно добавлен.");
        } catch (RuntimeException ex) {
            System.out.println("Не удалось добавить контакт. " + ex.getMessage());
        }
    }

    protected void handleUpdateContact() {
        System.out.println("Введите id контакта, которого хотите изменить: ");
        String contactId = scanner.nextLine().trim();
        Optional<Long> idOpt = parseId(contactId);
        if (idOpt.isEmpty()) {
            return;
        }

        long id = idOpt.get();

        Optional<Contact> currentContactOpt;
        try {
            currentContactOpt = contactDAO.findById(id);
        } catch (RuntimeException ex) {
            System.out.println("Не удалось найти контакт. " + ex.getMessage());
            return;
        }

        if (currentContactOpt.isEmpty()) {
            printContactNotFound(id);
            return;
        }

        System.out.println("Введите новые данные по контакту. Если изменения вносить не нужно, то оставьте поле пустым.");
        System.out.println("Новое имя контакта: ");
        String updateContactName = scanner.nextLine().trim();
        System.out.println("Новый номер телефона: ");
        String updatePhoneNumber = scanner.nextLine().trim();

        String newContactName = updateContactName.isBlank() ? currentContactOpt.get().getContactName() : updateContactName;
        String newPhoneNumber = updatePhoneNumber.isBlank() ? currentContactOpt.get().getNumber() : updatePhoneNumber;
        Contact updateContact = new Contact(newContactName, newPhoneNumber);

        System.out.println("Введите id категории, в которую хотите перенести контакт: ");
        String categoryIdString = scanner.nextLine().trim();
        Optional<Long> categoryIdOpt = categoryIdString.isBlank() ?
                Optional.of(currentContactOpt.get().getCategoryContact().getId()) : parseId(categoryIdString);

        if (categoryIdOpt.isEmpty()) {
            return;
        }

        long categoryId = categoryIdOpt.get();

        try {
            Optional<Contact> updated = contactDAO.updateContact(id, categoryId, updateContact);
            if (updated.isEmpty()) {
                printContactNotFound(id);
                return;
            }
            System.out.println("Контакт успешно обновлен.");
        } catch (RuntimeException ex) {
            System.out.println("Не удалось обновить контакт. " + ex.getMessage());
        }
    }

    protected void handleFindByIdContact() {
        System.out.println("Введите id контакта: ");
        String contactId = scanner.nextLine().trim();
        Optional<Long> idOpt = parseId(contactId);
        if (idOpt.isEmpty()) {
            return;
        }

        long id = idOpt.get();

        try {
            Optional<Contact> contact = contactDAO.findById(id);
            if (contact.isEmpty()) {
                printContactNotFound(id);
                return;
            }
            System.out.println("Найден контакт: " + contact.get());
        } catch (RuntimeException ex) {
            System.out.println("Не удалось найти контакт. " + ex.getMessage());
        }
    }

    protected void handleFindAllContactByUserId() {
        System.out.println("Введите id пользователя: ");
        String userId = scanner.nextLine().trim();
        Optional<Long> idOpt = parseId(userId);
        if (idOpt.isEmpty()) {
            return;
        }

        long id = idOpt.get();

        try {
            List<Contact> contacts = contactDAO.findAllContactByUserId(id);
            if (contacts.isEmpty()) {
                System.out.println("У пользователя нет сохраненных контактов.");
                return;
            }
            System.out.println("Список всех контактов: ");
            contacts.sort(Comparator.comparing(Contact::getContactName));
            for (Contact contact : contacts) {
                System.out.println(contact);
            }
        } catch (RuntimeException ex) {
            System.out.println("Не удалось получить список контактов. " + ex.getMessage());
        }
    }

    protected void handleDeleteContact() {
        System.out.println("Введите id контакта: ");
        String contactId = scanner.nextLine().trim();
        Optional<Long> idOpt = parseId(contactId);
        if (idOpt.isEmpty()) {
            return;
        }
        long id = idOpt.get();

        try {
            if (contactDAO.deleteContact(id)) {
                System.out.println("Контакт успешно удален.");
            } else {
                printContactNotFound(id);
            }
        } catch (RuntimeException ex) {
            System.out.println("Не удалось удалить контакт. " + ex.getMessage());
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

    private void printContactNotFound(long id) {
        System.out.println("Контакт с id = " + id + " не найден.");
    }
}
