package ru.vk.etcos.tasklist.auth.controller;

import java.util.*;

import jakarta.validation.*;
import lombok.extern.java.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.*;
import org.springframework.web.bind.annotation.*;
import ru.vk.etcos.tasklist.auth.entity.*;
import ru.vk.etcos.tasklist.auth.exception.*;
import ru.vk.etcos.tasklist.auth.exception.RoleNotFoundException;
import ru.vk.etcos.tasklist.auth.service.*;
import ru.vk.etcos.tasklist.auth.utils.*;

@RestController
@RequestMapping("/auth")
@Log
public class AuthController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JWTUtils jwtUtils;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTUtils jwtUtils) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/test")
    public String test() {
        return "OK";
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

    @PostMapping("/activate-account")
    public ResponseEntity<Boolean> activateUser(@RequestBody String uuid) {
        CActivity activity = userService.findActivityByUuid(uuid)
            .orElseThrow(() -> new UsernameNotFoundException("Activity Not Found with uuid: " + uuid));

        if (activity.isActivated()) {
            throw new UserAlreadyActivatedException("User already activated");
        }

        int updateCount = userService.activate(uuid);

        return ResponseEntity.ok(updateCount == 1);
    }

    @PostMapping("/login")
    public ResponseEntity<CUser> login(@Valid @RequestBody CUser user) {

        Authentication authenticate = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authenticate);

        UserDetailsImpl userDetails = (UserDetailsImpl) authenticate.getPrincipal();

        if (userDetails.isActivated()) {
            // после каждого успешного входа генерируется новый jwt, чтобы последующие запросы на backend авторизовать автоматически
            String jwt = jwtUtils.createAccessToken(userDetails.getUser());


            return ResponseEntity.ok(userDetails.getUser());
        } else {
            throw new DisabledException("User disabled");
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonException> handleExceptions(Exception ex) {
        return new ResponseEntity<>(new JsonException(ex.getClass().getSimpleName()), HttpStatus.BAD_REQUEST);
    }

}
