package ru.vk.etcos.tasklist.auth.controller;

import java.util.*;

import jakarta.validation.*;
import lombok.extern.java.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
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
    private CookieUtils cookieUtils;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
            JWTUtils jwtUtils, CookieUtils cookieUtils) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.cookieUtils = cookieUtils;
    }

    // TODO for test
    @PostMapping("/test-no-auth")
    public String testNoAuth() {
        return "OK NO AUTH";
    }

    // TODO for test
    @PreAuthorize("USER")
    @PostMapping("/test-with-auth")
    public String testWithAuth() {
        return "OK WITH AUTH";
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
            // пароль нужен только для аутентификации - нужно занулить, чтобы не засветить его
            userDetails.getUser().setPassword(null);

            // после каждого успешного входа генерируется новый jwt, чтобы последующие запросы на backend авторизовать автоматически
            String jwt = jwtUtils.createAccessToken(userDetails.getUser());

            // создаем кук со значением jwt (браузер будет отправлять его автоматически на backend при каждом запросе)
            HttpCookie cookie = cookieUtils.createJWTCookie(jwt);

            // объект для добавления заголовков в response
            HttpHeaders responseHeaders = new HttpHeaders();

            // добавляем кук в заголовок
            responseHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString());

            // отправляем клиенту данные пользователя (и jwt-кук в заголовке Set-Cookie)
            return ResponseEntity.ok().headers(responseHeaders).body(userDetails.getUser());
        } else {
            throw new DisabledException("User disabled");
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonException> handleExceptions(Exception ex) {
        return new ResponseEntity<>(new JsonException(ex.getClass().getSimpleName()), HttpStatus.BAD_REQUEST);
    }

}
