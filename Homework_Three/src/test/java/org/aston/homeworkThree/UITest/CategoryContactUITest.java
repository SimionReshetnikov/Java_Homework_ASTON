package org.aston.homeworkThree.UITest;

import org.aston.homeworkThree.dao.CategoryContactDAO;
import org.aston.homeworkThree.model.CategoryContact;
import org.aston.homeworkThree.userInterface.CategoryContactUI;
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
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class CategoryContactUITest {
    @Mock
    private CategoryContactDAO categoryContactDAO;
    private CategoryContactUI categoryContactUI;

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
        categoryContactUI = new CategoryContactUI(categoryContactDAO, scanner);
    }

    private String getOutput() {
        return outputStream.toString();
    }

    @Test
    void testHandleSaveCategorySuccess() {
        setInput("Work\n");

        categoryContactUI.handleSaveCategoryContact();

        assertTrue(getOutput().contains("Категория контактов успешно добавлена"));
        verify(categoryContactDAO, times(1)).saveCategory(any(CategoryContact.class));
    }

    @Test
    void testHandleSaveCategoryBlankCategoryName() {
        setInput("\n");

        categoryContactUI.handleSaveCategoryContact();

        assertTrue(getOutput().contains("Наименование категории контактов не может быть пустым"));
        verify(categoryContactDAO, never()).saveCategory(any(CategoryContact.class));
    }

    @Test
    void testHandleSaveCategoryDaoThrowsException() {
        setInput("Work\n");
        doThrow(new RuntimeException()).when(categoryContactDAO).saveCategory(any(CategoryContact.class));

        categoryContactUI.handleSaveCategoryContact();

        assertTrue(getOutput().contains("Не удалось добавить категорию контактов"));
    }

    @Test
    void testHandleUpdateCategoryContactSuccess() {
        setInput("1\nWork\n");
        CategoryContact existing = new CategoryContact("OldName");
        CategoryContact updated = new CategoryContact("Work");

        when(categoryContactDAO.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryContactDAO.updateCategory(eq(1L), any(CategoryContact.class))).thenReturn(Optional.of(updated));

        categoryContactUI.handleUpdateCategoryContact();

        assertTrue(getOutput().contains("Категория успешно изменена"));
        verify(categoryContactDAO, times(1))
                .updateCategory(eq(1L), any(CategoryContact.class));
    }

    @Test
    void testHandleUpdateCategoryNotFound() {
        setInput("999\nWork\n");
        when(categoryContactDAO.findById(999L)).thenReturn(Optional.empty());

        categoryContactUI.handleUpdateCategoryContact();

        assertTrue(getOutput().contains("Категория с id = 999 не найдена"));
        verify(categoryContactDAO, never()).updateCategory(anyLong(), any(CategoryContact.class));
    }

    @Test
    void testHandleUpdateCategoryBlankNameKeepsOld() {
        setInput("1\n\n");
        CategoryContact existing = new CategoryContact("Work");
        when(categoryContactDAO.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryContactDAO.updateCategory(eq(1L), any(CategoryContact.class))).thenReturn(Optional.of(existing));

        categoryContactUI.handleUpdateCategoryContact();

        assertTrue(getOutput().contains("Категория успешно изменена"));
        verify(categoryContactDAO, times(1))
                .updateCategory(eq(1L), any(CategoryContact.class));
    }

    @Test
    void testHandleUpdateCategoryInvalidId() {
        setInput("abc\nWork\n");

        categoryContactUI.handleUpdateCategoryContact();

        assertTrue(getOutput().contains("Некорректное значение id"));
        verify(categoryContactDAO, never()).updateCategory(anyLong(), any(CategoryContact.class));
    }

    @Test
    void testHandleUpdateCategoryDuplicateName() {
        setInput("1\nWork\n");
        CategoryContact existing = new CategoryContact("Friends");

        when(categoryContactDAO.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryContactDAO.updateCategory(eq(1L), any(CategoryContact.class)))
                .thenThrow(new IllegalStateException("Категория с именем Work уже существует"));

        categoryContactUI.handleUpdateCategoryContact();

        assertTrue(getOutput().contains("Не удалось обновить категорию"));
        assertTrue(getOutput().contains("уже существует"));
        verify(categoryContactDAO, times(1))
                .updateCategory(eq(1L), any(CategoryContact.class));
    }

    @Test
    void testHandleFindByIdCategorySuccess() {
        setInput("1\n");
        CategoryContact mockContact = new CategoryContact("Work");
        when(categoryContactDAO.findById(1L)).thenReturn(Optional.of(mockContact));

        categoryContactUI.handleFindByIdCategoryContact();

        assertTrue(getOutput().contains("Найдена категория контактов"));
        verify(categoryContactDAO, times(1)).findById(1L);
    }

    @Test
    void testHandleFindByIdCategoryNotFound() {
        setInput("999\n");
        when(categoryContactDAO.findById(999L)).thenReturn(Optional.empty());

        categoryContactUI.handleFindByIdCategoryContact();

        assertTrue(getOutput().contains("Категория с id = 999 не найдена"));
        verify(categoryContactDAO, times(1)).findById(999L);
    }

    @Test
    void testHandleFindByIdCategoryInvalidId() {
        setInput("abc\n");

        categoryContactUI.handleFindByIdCategoryContact();

        assertTrue(getOutput().contains("Некорректное значение id"));
        verify(categoryContactDAO, never()).findById(anyLong());
    }

    @Test
    void testHandleFindAllCategorySuccess() {
        setInput("");
        List<CategoryContact> categories = List.of(
                new CategoryContact("Work"),
                new CategoryContact("Friends")
        );
        when(categoryContactDAO.findAll()).thenReturn(categories);

        categoryContactUI.handleFindAllCategoryContact();

        assertTrue(getOutput().contains("Список категорий контактов"));
    }

    @Test
    void testHandleFindAllCategoryEmpty() {
        setInput("");
        when(categoryContactDAO.findAll()).thenReturn(List.of());

        categoryContactUI.handleFindAllCategoryContact();

        assertTrue(getOutput().contains("Список категорий контактов пуст"));
    }

    @Test
    void testHandleFindAllCategoryDaoThrowsException() {
        setInput("");
        doThrow(RuntimeException.class).when(categoryContactDAO).findAll();

        categoryContactUI.handleFindAllCategoryContact();

        assertTrue(getOutput().contains("Не удалось получить список категорий контактов"));
    }

    @Test
    void testHandleRemoveCategorySuccess() {
        setInput("1\n");
        when(categoryContactDAO.deleteCategory(1L)).thenReturn(true);

        categoryContactUI.handleRemoveCategoryContact();

        assertTrue(getOutput().contains("Категория контактов успешно удалена"));
        verify(categoryContactDAO, times(1)).deleteCategory(1L);
    }

    @Test
    void testHandleRemoveCategoryNotFound() {
        setInput("999\n");
        when(categoryContactDAO.deleteCategory(999L)).thenReturn(false);

        categoryContactUI.handleRemoveCategoryContact();

        assertTrue(getOutput().contains("Категория с id = 999 не найдена"));
        verify(categoryContactDAO, times(1)).deleteCategory(999L);
    }

    @Test
    void testHandleRemoveCategoryWithLinkedContacts() {
        setInput("1\n");
        when(categoryContactDAO.deleteCategory(1L))
                .thenThrow(new IllegalStateException("Невозможно удалить категорию. К ней привязано 2 контактов."));

        categoryContactUI.handleRemoveCategoryContact();

        assertTrue(getOutput().contains("Не удалось удалить категорию контактов"));
        verify(categoryContactDAO, times(1)).deleteCategory(1L);
    }

    @Test
    void testHandleRemoveCategoryInvalidId() {
        setInput("abc\n");

        categoryContactUI.handleRemoveCategoryContact();

        assertTrue(getOutput().contains("Некорректное значение id"));
        verify(categoryContactDAO, never()).deleteCategory(anyLong());
    }
}
