package org.aston.homework_four.test_controller;

import org.aston.homework_four.DTO.request.UserRequestDto;
import org.aston.homework_four.DTO.response.UserResponseDto;
import org.aston.homework_four.controller.UserController;
import org.aston.homework_four.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void testCreateUser_statusOk() throws Exception {
        UserResponseDto response = new UserResponseDto(
                1L, "Michael", "michael@example.com", 30, LocalDateTime.now()
        );
        String newUserJson = """
                {
                    "name": "Michael",
                    "email": "michael@example.com",
                    "age": 30
                }
                """;

        when(userService.saveUser(any(UserRequestDto.class))).thenReturn(response);


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.email").value(response.getEmail()))
                .andExpect(jsonPath("$.age").value(response.getAge()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void testCreateUserWithBlankName_statusBadRequest() throws Exception {
        String newUserJson = """
                {
                    "name": "",
                    "email": "michael@example.com",
                    "age": 30
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserWithBlankEmail_statusBadRequest() throws Exception {
        String newUserJson = """
                {
                    "name": "Michael",
                    "email": "",
                    "age": 30
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserWithInvalidAge_statusBadRequest() throws Exception {
        String newUserJson = """
                {
                    "name": "Michael",
                    "email": "michael@example.com",
                    "age": 500
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUser_statusOk() throws Exception {
        UserResponseDto response = new UserResponseDto(
                1L, "John", "john@example.com", 45, LocalDateTime.now()
        );
        String newUserJson = """
                {
                    "name": "Old",
                    "email": "old@example.com",
                    "age": 55
                }
                """;

        when(userService.updateUser(eq(1L), any(UserRequestDto.class))).thenReturn(Optional.of(response));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.email").value(response.getEmail()))
                .andExpect(jsonPath("$.age").value(response.getAge()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void testUpdateUserWithUserNotFound_statusNotFound() throws Exception {
        long updateUserId = 999L;
        String newUserJson = """
                {
                    "name": "Harry",
                    "email": "harry@example.com",
                    "age": 55
                }
                """;
        when(userService.updateUser(eq(updateUserId), any(UserRequestDto.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUserWithInvalidUpdateUser_statusBadRequest() throws Exception {
        String newUserJson = """
                {
                    "name": "",
                    "email": "harry.com",
                    "age": 777
                }
                """;

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserById_statusOk() throws Exception {
        UserResponseDto response = new UserResponseDto(
                1L, "Harry", "harry@example.com", 55, LocalDateTime.now()
        );

        when(userService.findById(eq(1L))).thenReturn(Optional.of(response));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.email").value(response.getEmail()))
                .andExpect(jsonPath("$.age").value(response.getAge()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void testGetUserById_statusNotFound() throws Exception {
        long userId = 999L;
        when(userService.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserByIdWithInvalidUserId_statusBadRequest() throws Exception {
        mockMvc.perform(get("/users/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllUsers_statusOk() throws Exception {
        UserResponseDto responseOne = new UserResponseDto(
                1L, "John", "john@example.com", 40, LocalDateTime.now()
        );

        UserResponseDto responseTwo = new UserResponseDto(
                2L, "Mike", "mike@example.com", 54, LocalDateTime.now()
        );

        List<UserResponseDto> users = List.of(responseOne, responseTwo);

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(responseOne.getId()))
                .andExpect(jsonPath("$[0].name").value(responseOne.getName()))
                .andExpect(jsonPath("$[0].email").value(responseOne.getEmail()))
                .andExpect(jsonPath("$[1].id").value(responseTwo.getId()))
                .andExpect(jsonPath("$[1].name").value(responseTwo.getName()))
                .andExpect(jsonPath("$[1].email").value(responseTwo.getEmail()));
    }

    @Test
    void testDeleteUser_statusOk() throws Exception {
        long userId = 1L;
        when(userService.deleteUser(userId)).thenReturn(true);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUser_statusNotFound() throws Exception {
        long userId = 999L;
        when(userService.deleteUser(userId)).thenReturn(false);

        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound());
    }
}
