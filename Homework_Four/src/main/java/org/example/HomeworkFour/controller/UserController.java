package org.example.HomeworkFour.controller;

import org.example.HomeworkFour.model.User;
import org.example.HomeworkFour.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseBody
    public Optional<User> createUser(@RequestBody User user) {
        return userService.saveUser(user);
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
