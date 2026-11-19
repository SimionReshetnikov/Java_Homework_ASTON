package org.example.HomeworkFour.controller;

import jakarta.validation.Valid;
import org.example.HomeworkFour.DTO.request.UserRequestDto;
import org.example.HomeworkFour.DTO.response.UserResponseDto;
import org.example.HomeworkFour.model.User;
import org.example.HomeworkFour.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDto createUser(@Valid @RequestBody UserRequestDto userRequestDTO) {
        return userService.saveUser(userRequestDTO);
    }

    @PutMapping("/{id}")
    public Optional<User> updateUser(@RequestParam long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @GetMapping("/{id}")
    public Optional<User> findById(@RequestParam long id) {
        return userService.findById(id);
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@RequestParam long id) {
        userService.deleteUser(id);
    }
}
