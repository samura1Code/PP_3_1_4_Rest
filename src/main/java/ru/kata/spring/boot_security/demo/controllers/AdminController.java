package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;


    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping("/users")
    public String showUsers(Model model) {
        model.addAttribute("users", userService.getUsers());
        return "admin";
    }

    @GetMapping("/newUser")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.findAll()); // добавлено для получения всех ролей
        return "newUser";
    }

    @PostMapping("/newUser")
    public String create(@ModelAttribute("user") User user) {
        userService.createUser(user);
        return "redirect:/admin/users";
    }


    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long userId) {
        userService.deleteUser(userId);
        return "redirect:/admin/users";
    }

    @GetMapping("/update/{id}")
    public String updateUserForm(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin/users?error=UserNotFound";
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.findAll());
        return "userupdate";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long id, @ModelAttribute User user) {
        userService.updateUser(id, user);
        return "redirect:/admin/users";
    }
}
