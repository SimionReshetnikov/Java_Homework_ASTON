package org.aston.homeworkThree.UITest;

import org.aston.homeworkThree.dao.UserDAO;
import org.aston.homeworkThree.model.User;
import org.aston.homeworkThree.userInterface.UserUI;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserUITest {
    @Mock
    private UserDAO userDAO;
    private UserUI userUI;

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
        userUI = new UserUI(userDAO, scanner);
    }

    private String getOutput() {
        return outputStream.toString();
    }

    @Test
    void testHandleSaveUserSuccess() {
        setInput("Nil\nArmstrong\n");

        userUI.handleSaveUser();

        assertTrue(getOutput().contains("Пользователь успешно добавлен"));
        verify(userDAO, times(1)).saveUser(any(User.class));
    }

    @Test
    void testHandleSaveUserBlankFirstName() {
        setInput("\nArmstrong\n");

        userUI.handleSaveUser();

        assertTrue(getOutput().contains("Имя или фамилия не могут быть пустыми"));
        verify(userDAO, never()).saveUser(any(User.class));
    }

    @Test
    void testHandleSaveUserBlankLastName() {
        setInput("Nil\n\n");

        userUI.handleSaveUser();

        assertTrue(getOutput().contains("Имя или фамилия не могут быть пустыми"));
        verify(userDAO, never()).saveUser(any(User.class));
    }

    @Test
    void testHandleSaveUserDAOThrowsException() {
        setInput("Nil\nArmstrong\n");
        doThrow(new RuntimeException()).when(userDAO).saveUser(any(User.class));

        userUI.handleSaveUser();

        assertTrue(getOutput().contains("Не удалось добавить пользователя"));
    }

    @Test
    void testHandleFindByIdSuccess() {
        setInput("1\n");
        User mockUser = new User("Nil", "Armstrong");
        when(userDAO.findById(1L)).thenReturn(Optional.of(mockUser));

        userUI.handleFindById();
        verify(userDAO, times(1)).findById(1L);
    }

    @Test
    void testHandleFindByIdNotFound() {
        setInput("999\n");
        when(userDAO.findById(999L)).thenReturn(Optional.empty());

        userUI.handleFindById();

        assertTrue(getOutput().contains("Пользователь с id = 999 не найден"));
        verify(userDAO, times(1)).findById(999L);
    }

    @Test
    void testHandleFindByIdInvalidId() {
        setInput("abc\n");

        userUI.handleFindById();

        assertTrue(getOutput().contains("Некорректное значение id"));
        verify(userDAO, never()).findById(anyLong());
    }

    @Test
    void testHandleFindAllEmptyList() {
        setInput("");
        when(userDAO.findAll()).thenReturn(List.of());

        userUI.handleFindAll();

        assertTrue(getOutput().contains("Список пользователей пуст"));
    }

    @Test
    void testHandleFindAllWithUsers() {
        setInput("");
        List<User> users = List.of(
                new User("Nil", "Armstrong"),
                new User("Harry", "Potter")
        );
        when(userDAO.findAll()).thenReturn(users);

        userUI.handleFindAll();

        assertTrue(getOutput().contains("Список пользователей"));
        verify(userDAO, times(1)).findAll();
    }

    @Test
    void testHandleUpdateUserSuccess() {
        setInput("1\nNil\nArmstrong");
        User existing = new User("OldName", "OldLastName");
        User updated = new User("Nil", "Armstrong");

        when(userDAO.findById(1L)).thenReturn(Optional.of(existing));
        when(userDAO.updateUser(eq(1L), any(User.class))).thenReturn(Optional.of(updated));

        userUI.handleUpdateUser();

        assertTrue(getOutput().contains("Информация о пользователе успешно изменена"));
        verify(userDAO, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    void testHandleUpdateUserNotFound() {
        setInput("999\nHarry\nPotter\n");
        when(userDAO.findById(999L)).thenReturn(Optional.empty());

        userUI.handleUpdateUser();

        assertTrue(getOutput().contains("Пользователь с id = 999 не найден"));
        verify(userDAO, never()).updateUser(anyLong(), any(User.class));
    }

    @Test
    void testHandleRemoveUserSuccess() {
        setInput("1\n");
        when(userDAO.removeUser(1L)).thenReturn(true);

        userUI.handleRemoveUser();

        assertTrue(getOutput().contains("Пользователь успешно удален"));
        verify(userDAO, times(1)).removeUser(1L);
    }

    @Test
    void testHandleRemoveUserNotFound() {
        setInput("999\n");
        when(userDAO.removeUser(999L)).thenReturn(false);

        userUI.handleRemoveUser();

        assertTrue(getOutput().contains("Пользователь с id = 999 не найден"));
    }

    @Test
    void testHandleRemoveUserInvalidId() {
        setInput("abc\n");

        userUI.handleRemoveUser();

        assertTrue(getOutput().contains("Некорректное значение id"));
        verify(userDAO, never()).removeUser(anyLong());
    }
}
