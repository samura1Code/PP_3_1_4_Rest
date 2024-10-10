package ru.kata.spring.boot_security.demo.services;

import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final RoleService roleService;



    @Autowired
    public UserServiceImp(UserRepository userRepository, BCryptPasswordEncoder encoder, RoleRepository roleRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.roleService = roleService;
    }

    @Transactional
    @Override
    public List<User> getUsers() {

        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Transactional
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Transactional
    @Override
    public void createUser(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role userRole = roleService.getByUsername("ROLE_USER");
            user.setRoles(Set.of(userRole));
        }
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + user.getId()));

        existingUser.setUsername(user.getUsername());

        // Обновление пароля
        if (StringUtils.isNotBlank(user.getPassword())) {
            existingUser.setPassword(encoder.encode(user.getPassword()));
        }

        // Обновление ролей
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<Role> roles = user.getRoles().stream()
                    .map(role -> roleService.getRoleById(role.getId()))
                    .collect(Collectors.toSet());
            existingUser.setRoles(roles);
        }

        return userRepository.save(existingUser);
    }



    @Transactional
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

}
