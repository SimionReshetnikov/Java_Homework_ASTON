package org.aston.homeworkThree.UITest;

import org.aston.homeworkThree.dao.CategoryContactDAO;
import org.aston.homeworkThree.dao.ContactDAO;
import org.aston.homeworkThree.dao.UserDAO;
import org.aston.homeworkThree.model.CategoryContact;
import org.aston.homeworkThree.model.Contact;
import org.aston.homeworkThree.model.User;
import org.aston.homeworkThree.userInterface.ContactUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContactUITest {
    @Mock
    private CategoryContactDAO categoryContactDAO;

    @Mock
    private ContactDAO contactDAO;

    @Mock
    private UserDAO userDAO;
    private ContactUI contactUI;

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private void setInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        contactUI = new ContactUI(contactDAO, scanner);
    }

    private String getOutput() {
        return outputStream.toString();
    }

    @Test
    void testHandleSaveContactSuccess() {
        setInput("John\n+79991234567\n1\n2\n");

        contactUI.handleSaveContact();

        assertTrue(getOutput().contains("Контакт успешно добавлен"));
        verify(contactDAO, times(1)).saveContact(any(Contact.class), eq(1L), eq(2L));
    }

    @Test
    void testHandleSaveContactBlankName() {
        setInput("\n+79991234567\n1\n2\n");

        contactUI.handleSaveContact();

        assertTrue(getOutput().contains("Имя или номер телефона контакта не могут быть пустыми"));
        verify(contactDAO, never()).saveContact(any(), anyLong(), anyLong());
    }

    @Test
    void testHandleSaveContactBlankNumber() {
        setInput("John\n\n1\n2\n");

        contactUI.handleSaveContact();

        assertTrue(getOutput().contains("Имя или номер телефона контакта не могут быть пустыми"));
        verify(contactDAO, never()).saveContact(any(), anyLong(), anyLong());
    }

    @Test
    void testHandleSaveContactInvalidUserId() {
        setInput("John\n+79991234567\nabc\n2\n");

        contactUI.handleSaveContact();

        assertTrue(getOutput().contains("Некорректное значение id"));
        verify(contactDAO, never()).saveContact(any(), anyLong(), anyLong());
    }

    @Test
    void testHandleSaveContactInvalidCategoryId() {
        setInput("John\n+79991234567\n1\nabc\n");

        contactUI.handleSaveContact();

        assertTrue(getOutput().contains("Некорректное значение id"));
        verify(contactDAO, never()).saveContact(any(), anyLong(), anyLong());
    }

    @Test
    void testHandleSaveContactDaoThrowsException() {
        setInput("John\n+79991234567\n1\n2\n");

        doThrow(new RuntimeException("Ошибка БД")).when(contactDAO)
                .saveContact(any(Contact.class), anyLong(), anyLong());

        contactUI.handleSaveContact();

        assertTrue(getOutput().contains("Не удалось добавить контакт"));
        verify(contactDAO, times(1)).saveContact(any(Contact.class), anyLong(), anyLong());
    }

    @Test
    void testHandleUpdateContactSuccess() {
        setInput("1\nJohn\n+79991234567\n2\n");
        Contact existing = new Contact("OldName", "+00000000000");
        existing.setId(1L);
        CategoryContact oldCategory = new CategoryContact("OLD");
        oldCategory.setId(3L);
        existing.setCategoryContact(oldCategory);

        when(contactDAO.findById(1L)).thenReturn(Optional.of(existing));
        when(contactDAO.updateContact(eq(1L), eq(2L), any(Contact.class)))
                .thenReturn(Optional.of(existing));

        contactUI.handleUpdateContact();

        assertTrue(getOutput().contains("Контакт успешно обновлен"));
        verify(contactDAO, times(1)).updateContact(eq(1L), eq(2L), any(Contact.class));
    }

    @Test
    void testHandleUpdateContactNotFound() {
        setInput("999\nJohn\n+79991234567\n2\n");
        when(contactDAO.findById(999L)).thenReturn(Optional.empty());

        contactUI.handleUpdateContact();

        assertTrue(getOutput().contains("Контакт с id = 999 не найден"));
        verify(contactDAO, never()).updateContact(anyLong(), anyLong(), any());
    }

    @Test
    void testHandleUpdateContactBlankFieldsKeepOld() {
        setInput("1\n\n\n\n");
        Contact existing = new Contact("OldName", "+79991111111");
        existing.setId(1L);
        CategoryContact oldCategory = new CategoryContact("OLD");
        oldCategory.setId(3L);
        existing.setCategoryContact(oldCategory);

        when(contactDAO.findById(1L)).thenReturn(Optional.of(existing));
        when(contactDAO.updateContact(eq(1L), eq(3L), any(Contact.class)))
                .thenReturn(Optional.of(existing));

        contactUI.handleUpdateContact();

        assertTrue(getOutput().contains("Контакт успешно обновлен"));
        verify(contactDAO, times(1)).updateContact(eq(1L), eq(3L), any(Contact.class));
    }

    @Test
    void testHandleUpdateContactInvalidId() {
        setInput("abc\nJohn\n+79991234567\n2\n");

        contactUI.handleUpdateContact();

        assertTrue(getOutput().contains("Некорректное значение id"));
        verify(contactDAO, never()).updateContact(anyLong(), anyLong(), any());
    }

    @Test
    void testHandleUpdateContactInvalidCategoryId() {
        setInput("1\nNewName\n+79990000000\nabc\n");
        Contact existing = new Contact("OldName", "+79991111111");
        existing.setId(1L);
        CategoryContact oldCategory = new CategoryContact("OLD");
        oldCategory.setId(3L);
        existing.setCategoryContact(oldCategory);

        when(contactDAO.findById(1L)).thenReturn(Optional.of(existing));

        contactUI.handleUpdateContact();

        assertTrue(getOutput().contains("Некорректное значение id"));
        verify(contactDAO, never()).updateContact(anyLong(), anyLong(), any());
    }

    @Test
    void testHandleFindByIdContactSuccess() {
        setInput("1\n");
        Contact contact = new Contact("John", "+79991234567");
        contact.setId(1L);
        User user = new User("Michael", "Jackson");
        user.setId(2L);
        CategoryContact categoryContact = new CategoryContact("Work");
        categoryContact.setId(3L);
        contact.setUser(user);
        contact.setCategoryContact(categoryContact);

        when(contactDAO.findById(1L)).thenReturn(Optional.of(contact));

        contactUI.handleFindByIdContact();

        assertTrue(getOutput().contains("Найден контакт"));
        verify(contactDAO, times(1)).findById(1L);
    }

    @Test
    void testHandleFindByIdContactNotFound() {
        setInput("999\n");
        when(contactDAO.findById(999L)).thenReturn(Optional.empty());

        contactUI.handleFindByIdContact();

        assertTrue(getOutput().contains("Контакт с id = 999 не найден"));
        verify(contactDAO, times(1)).findById(999L);
    }

    @Test
    void testHandleFindByIdContactInvalidId() {
        setInput("abc\n");

        contactUI.handleFindByIdContact();

        assertTrue(getOutput().contains("Некорректное значение id"));
        verify(contactDAO, never()).findById(anyLong());
    }

    @Test
    void testHandleFindAllContactByUserIdSuccess() {
        setInput("1\n");
        List<Contact> contacts = List.of(
                new Contact("John", "+79991234567"),
                new Contact("Harry", "+70009876543")
        );

        when(contactDAO.findAllContactByUserId(1L)).thenReturn(contacts);

        contactUI.handleFindAllContactByUserId();

        assertTrue(getOutput().contains("Список всех контактов"));
        verify(contactDAO, times(1)).findAllContactByUserId(1L);
    }

    @Test
    void testHandleFindAllContactByUserEmpty() {
        setInput("1\n");
        when(contactDAO.findAllContactByUserId(1L)).thenReturn(List.of());

        contactUI.handleFindAllContactByUserId();

        assertTrue(getOutput().contains("У пользователя нет сохраненных контактов"));
        verify(contactDAO, times(1)).findAllContactByUserId(1L);
    }

    @Test
    void testHandleFindAllContactByUserIdInvalidId() {
        setInput("abc\n");

        contactUI.handleFindAllContactByUserId();

        assertTrue(getOutput().contains("Некорректное значение id"));
        verify(contactDAO, never()).findAllContactByUserId(anyLong());
    }

    @Test
    void testHandleDeleteContactSuccess() {
        setInput("1\n");
        when(contactDAO.deleteContact(1L)).thenReturn(true);

        contactUI.handleDeleteContact();

        assertTrue(getOutput().contains("Контакт успешно удален"));
        verify(contactDAO, times(1)).deleteContact(1L);
    }

    @Test
    void testHandleDeleteContactNotFound() {
        setInput("999\n");
        when(contactDAO.deleteContact(999L)).thenReturn(false);

        contactUI.handleDeleteContact();

        assertTrue(getOutput().contains("Контакт с id = 999 не найден"));
        verify(contactDAO, times(1)).deleteContact(999L);
    }

    @Test
    void testHandleDeleteContactInvalidId() {
        setInput("abc\n");

        contactUI.handleDeleteContact();

        assertTrue(getOutput().contains("Некорректное значение id"));
        verify(contactDAO, never()).deleteContact(anyLong());
    }
}
