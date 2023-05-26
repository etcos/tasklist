package ru.vk.etcos.tasklist.auth.controller;

import jakarta.validation.*;
import lombok.extern.java.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.vk.etcos.tasklist.auth.entity.*;
import ru.vk.etcos.tasklist.auth.service.*;

@RestController
@RequestMapping("/auth")
@Log
public class AuthController {
    private UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/register")
    public ResponseEntity<CUser> register(@Valid @RequestBody CUser user) {

        CUser savedUser = userService.save(user);

        return ResponseEntity.ok(savedUser);
    }
}
