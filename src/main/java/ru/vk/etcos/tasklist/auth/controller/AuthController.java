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
    private EmailService emailService;
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
            JWTUtils jwtUtils, CookieUtils cookieUtils, EmailService emailService, UserDetailsServiceImpl userDetailsService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.cookieUtils = cookieUtils;
        this.emailService = emailService;
        this.userDetailsService = userDetailsService;
    }

    // TODO for test
    @PostMapping("/test-no-auth")
    public String testNoAuth() {
        return "OK NO AUTH";
    }

    // TODO for test
    @PreAuthorize("hasAuthority('USER')")
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

        // отправляем письмо о том, что нужно активировать аккаунт(выполняется в параллельном потоке)
        emailService.sendActivateEmail(user.getEmail(), user.getUsername(), activity.getUuid());

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

    @PostMapping("/reset-activate-email")
    public ResponseEntity resetActivateEmail(@RequestBody String usernameOrEmail) {
        // находим пользователя в БД (ищет как по email, так и по username)
        UserDetailsImpl user = (UserDetailsImpl) userDetailsService.loadUserByUsername(usernameOrEmail);

        // у каждого пользователя должна быть запись Activity (вся его активность) - если этого объекта нет - значит что-то пошло не так
        CActivity activity = userService.findActivityByUserId(user.getId())
            .orElseThrow(() -> new UserAlreadyActivatedException("Activity Not Found with user: " + user.getUsername()));

        if (activity.isActivated()) {
            throw new UserAlreadyActivatedException("User already activated: " + usernameOrEmail);
        }

        // отправляем письмо активации
        emailService.sendActivateEmail(user.getEmail(), user.getUsername(), activity.getUuid());

        return ResponseEntity.ok().build();
    }

    // отправка письма для сброса пароля
    @PostMapping("/send-email-reset-password")
    public ResponseEntity sendEmailResetPassword(@RequestBody String email) {
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(email);
        CUser user = userDetails.getUser();

        // отправляем письмо для сброса пароля
        emailService.sendResetPassword(user.getEmail(), jwtUtils.createEmailResetToken(user));

        return ResponseEntity.ok().build();
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

    // выход из системы - мы должны удалить кук с jwt (пользователю придется заново логиниться при следующем входе)
    @PostMapping("/logout")
    public ResponseEntity logout() {
        // создаем кук с истекшим сроком действия, тес самым браузер удалит такой кук автоматически
        HttpCookie cookie = cookieUtils.deleteJwtCookie();

        // создаем header и добавляем кук
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString());

        // добавляем header с куком в ответ и отправляем клиенту, браузер автоматически удалил кук
        return ResponseEntity.ok().headers(responseHeaders).build();
    }

    // обновление пароля
    @PostMapping("/update-password")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Boolean> updatePassword(@RequestBody String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // получаем пользователя из Spring контейнера
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

        // кол-во обновленных записей
        int updateCount = userService.updatePassword(passwordEncoder.encode(password), user.getUsername());

        return ResponseEntity.ok(updateCount == 1); // 1 - запись обновилась успешно, 0 - что-то пошло не так
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonException> handleExceptions(Exception ex) {
        return new ResponseEntity<>(new JsonException(ex.getClass().getSimpleName()), HttpStatus.BAD_REQUEST);
    }

}
