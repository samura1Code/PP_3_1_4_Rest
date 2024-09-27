package ru.kata.spring.boot_security.demo.controllers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.Set;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping()
    public String redirectToUsers() {
        return "redirect:admin/users";
    }

    @GetMapping("/users")
    public String adminProfile(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("roles", user.getAuthorities());
        return "admin";
    }

    @GetMapping("/")
    public String users(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("roles", user.getAuthorities());
        model.addAttribute("users", userService.getUsers());
        model.addAttribute("newUser", new User());
        return "admin";
    }



    @GetMapping("/new")
    public String newUser(Model model) {
        User user = new User();
        String roles = roleService.getAllRolesString();
        model.addAttribute("newUser", user);
        model.addAttribute("newRole", roles);
        return "redirect:/admin";
    }

    @PostMapping("/new")
    public String createUser(@ModelAttribute("newUser") User user, @RequestParam(value = "roles", required = false) Set<Long> roleIds) {
        if (roleIds != null) {
            Set<Role> roles = roleService.findAll().stream()
                    .filter(role -> roleIds.contains(role.getId()))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        userService.createUser(user);
        return "redirect:/admin";
    }


    @PostMapping("/update")
    public String updateUser(@ModelAttribute("updateUser") User user,
                             @RequestParam(value = "roles", required = false) Set<Long> roleIds) {
        User existingUser = userService.getUserById(user.getId());

        updateUsername(user, existingUser);
        updatePassword(user, existingUser);
        updateRoles(roleIds, existingUser);

        userService.updateUser(existingUser);
        return "redirect:/admin";
    }

    private void updateUsername(User user, User existingUser) {
        if (StringUtils.isNotBlank(user.getUsername())) {
            existingUser.setUsername(user.getUsername());
        }
    }

    private void updatePassword(User user, User existingUser) {
        if (StringUtils.isNotBlank(user.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
    }

    private void updateRoles(Set<Long> roleIds, User existingUser) {
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = roleService.findAll().stream()
                    .filter(role -> roleIds.contains(role.getId()))
                    .collect(Collectors.toSet());
            existingUser.setRoles(roles);
        }
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

}