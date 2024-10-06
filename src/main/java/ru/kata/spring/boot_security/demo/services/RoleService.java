package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.entity.Role;

import java.util.List;


public interface RoleService {

    void addRole(Role role);

    List<Role> findAll();

    String getAllRolesString();

    Role getByUsername(String username);

    Role getRoleById(Long id);
}
