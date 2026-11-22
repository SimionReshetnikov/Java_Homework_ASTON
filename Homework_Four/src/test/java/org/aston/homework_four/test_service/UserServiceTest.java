package org.aston.homework_four.test_service;

import org.aston.homework_four.DTO.request.UserRequestDto;
import org.aston.homework_four.DTO.response.UserResponseDto;
import org.aston.homework_four.model.User;
import org.aston.homework_four.repository.UserRepository;
import org.aston.homework_four.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
public class UserServiceTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("aston_homework_four_test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
    }

    @Test
    void testSaveUser() {
        UserRequestDto request = new UserRequestDto("John", 30, "john@example.com");

        UserResponseDto response = userService.saveUser(request);

        assertEquals("John", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals(30, response.getAge());
    }

    @Test
    void testSaveUserDuplicateEmailShouldThrowException() {
        UserRequestDto requestOne = new UserRequestDto("John", 30, "john@example.com");
        UserRequestDto requestTwo = new UserRequestDto("Harry", 30, "john@example.com");

        userService.saveUser(requestOne);
        assertThrows(DataIntegrityViolationException.class, () -> userService.saveUser(requestTwo));
    }

    @Test
    void testUpdateUser() {
        UserRequestDto updateUser = new UserRequestDto("John", 30, "john@example.com");
        UserRequestDto currentUser = new UserRequestDto("OldName", 99, "oldName@example.com");

        UserResponseDto response = userService.saveUser(currentUser);
        Optional<UserResponseDto> updated = userService.updateUser(response.getId(), updateUser);

        assertTrue(updated.isPresent());
        assertEquals(updateUser.getName(), updated.get().getName());
        assertEquals(updateUser.getAge(), updated.get().getAge());
        assertEquals(updateUser.getEmail(), updated.get().getEmail());

        Optional<User> fromDBUser = userRepository.findById(response.getId());
        assertTrue(fromDBUser.isPresent());
        assertEquals(updateUser.getName(), fromDBUser.get().getName());
        assertEquals(updateUser.getAge(), fromDBUser.get().getAge());
        assertEquals(updateUser.getEmail(), fromDBUser.get().getEmail());
    }

    @Test
    void testUpdateUserExistingId() {
        UserRequestDto updateUser = new UserRequestDto("John", 30, "john@example.com");
        UserRequestDto currentUser = new UserRequestDto("OldName", 99, "oldName@example.com");
        long id = 999L;

        userService.saveUser(currentUser);
        Optional<UserResponseDto> updated = userService.updateUser(id, updateUser);

        assertTrue(updated.isEmpty());
    }

    @Test
    void testFindById() {
        UserRequestDto request = new UserRequestDto("Michael", 45, "michael@example.com");
        long id = 1L;

        userService.saveUser(request);
        Optional<UserResponseDto> response = userService.findById(id);

        assertTrue(response.isPresent());
        assertEquals(id, response.get().getId());
        assertEquals(request.getName(), response.get().getName());
        assertEquals(request.getEmail(), response.get().getEmail());
        assertEquals(request.getAge(), response.get().getAge());
    }

    @Test
    void testFindByIdExistingId() {
        UserRequestDto request = new UserRequestDto("Michael", 45, "michael@example.com");
        long id = 999L;

        userService.saveUser(request);
        Optional<UserResponseDto> response = userService.findById(id);

        assertTrue(response.isEmpty());
    }

    @Test
    void testFindAll() {
        UserRequestDto requestOne = new UserRequestDto("Michael", 45, "michael@example.com");
        UserRequestDto requestTwo = new UserRequestDto("John", 30, "john@example.com");
        UserRequestDto requestThree = new UserRequestDto("Harry", 28, "harry@example.com");
        int sizeCollection = 3;

        userService.saveUser(requestOne);
        userService.saveUser(requestTwo);
        userService.saveUser(requestThree);
        List<UserResponseDto> userList = userService.findAll();

        assertFalse(userList.isEmpty());
        assertEquals(sizeCollection, userList.size());
    }

    @Test
    void testFindAllEmptyDatabase() {
        List<UserResponseDto> users = userService.findAll();
        assertTrue(users.isEmpty());
    }

    @Test
    void testDeleteUser() {
        UserRequestDto request = new UserRequestDto("Michael", 45, "michael@example.com");

        UserResponseDto saved = userService.saveUser(request);
        boolean deleted = userService.deleteUser(saved.getId());

        assertTrue(deleted);
        assertTrue(userRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    void testDeleteUserExistingId() {
        boolean deleted = userService.deleteUser(999L);

        assertFalse(deleted);
    }
}
