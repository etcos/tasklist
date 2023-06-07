package ru.vk.etcos.tasklist.auth.controller;

import java.util.*;

import jakarta.validation.*;
import lombok.extern.java.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.crypto.password.*;
import org.springframework.web.bind.annotation.*;
import ru.vk.etcos.tasklist.auth.entity.*;
import ru.vk.etcos.tasklist.auth.exception.*;
import ru.vk.etcos.tasklist.auth.exception.RoleNotFoundException;
import ru.vk.etcos.tasklist.auth.service.*;

@RestController
@RequestMapping("/auth")
@Log
public class AuthController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PutMapping("/register")
    public ResponseEntity<CUser> register(@Valid @RequestBody CUser user) {

        if (userService.userExistsByUsername(user.getUsername())) {
            throw new UsernameExistsException("Username already exists.");
        }

        if (userService.userExistsByEmail(user.getEmail())) {
            throw new EmailExistsException("Email already exists.");
        }

        CRole defaultRole = userService.findByName(UserService.DEFAULT_ROLE)
                .orElseThrow(() -> new RoleNotFoundException("Default Role USER not found."));
        user.getRoles().add(defaultRole);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        CActivity activity = new CActivity();
        activity.setUuid(UUID.randomUUID().toString());

        CUser savedUser = userService.register(user, activity);

        return ResponseEntity.ok(savedUser);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonException> handleExceptions(Exception ex) {
        return new ResponseEntity<>(new JsonException(ex.getClass().getSimpleName()), HttpStatus.BAD_REQUEST);
    }

}
