package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.security.Principal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;


@RestController
@RequestMapping("api/admin")
public class AdminRestController {
    private final UserService userService;
    private final RoleService roleService;
    private static final Logger logger = LoggerFactory.getLogger(AdminRestController.class);


    public AdminRestController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // Получение информации о текущем пользователе
    @GetMapping("/currentUser")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        logger.info("Запрос на получение текущего пользователя: {}", currentUser);
        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> showAllUsers() {
        List<User> allUsers = userService.getUsers();
        logger.info("Запрос на получение всех пользователей. Всего пользователей: {}", allUsers.size());
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @GetMapping("users/roles")
    public ResponseEntity<List<Role>> showAllRoles() {
        List<Role> allRoles = roleService.findAll();
        logger.info("Запрос на получение всех ролей. Всего ролей: {}", allRoles.size());
        return new ResponseEntity<>(allRoles, HttpStatus.OK);
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        logger.info("Запрос на получение пользователя с ID: {}", id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<User> addNewUser(@Valid @RequestBody User user) {
        userService.createUser(user);
        logger.info("Новый пользователь создан: {}", user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@Valid @PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        logger.info("Пользователь обновлен: {}", updatedUser);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        logger.info("Пользователь с ID {} был удален", id);
        return new ResponseEntity<>("User with ID " + id + " was deleted", HttpStatus.OK);
    }
}
