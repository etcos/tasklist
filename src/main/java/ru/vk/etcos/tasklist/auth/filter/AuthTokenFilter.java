package ru.vk.etcos.tasklist.auth.filter;

import java.io.*;
import java.util.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.*;
import org.springframework.security.web.authentication.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;
import org.springframework.web.filter.*;
import ru.vk.etcos.tasklist.auth.entity.*;
import ru.vk.etcos.tasklist.auth.exception.*;
import ru.vk.etcos.tasklist.auth.service.*;
import ru.vk.etcos.tasklist.auth.utils.*;
import ru.vk.etcos.tasklist.util.*;

/*
Все входящие запросы сначала обрабатывает фильтр AuthTokenFilter: он проверяет URI, если необходимо - считывает jwt из кука.
Если запрос пришел на публичную ссылку (авторизация, запрос на обновление пароля и пр.), то JWT не требуется и просто продолжается выполнение запроса.
Если запрос пришел на закрытую ссылку (только для авторизованных пользователей) - сначала фильтр AuthTokenFilter должен получить JWT из кука.
После получения и валидации jwt фильтр AuthTokenFilter аутентифицирует пользователя и добавляет его в Spring контейнер.
Только после этого - запрос передается дальше в контроллер для выполнения.
*/
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private JWTUtils jwtUtils;
    private CookieUtils cookieUtils;

    // Список адресов(URL) для которых не требуется авторизация (не будет проверяться JWT)
    // открытое API
    private List<String> permitURI = Arrays.asList(
        "test-no-auth", // тест
        "index", // отдельная главная страница?
        "register", // регистрация нового пользователя
        "login", // аутентификация (логин-пароль)
        "activate-account", // активация нового пользователя
        "reset-activate-email", // повторная отправка активации
        "send-email-reset-password" // отправка письма на сброс пароля
    );

    @Autowired
    public void setJwtUtils(JWTUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Autowired
    public void setCookieUtils(CookieUtils cookieUtils) {
        this.cookieUtils = cookieUtils;
    }

    // Этот метод вызывается автоматически при каждом входящем запросе
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Проверяем запрос идет на защищенную страницу или нет
        boolean isRequestToProtectedAPI = permitURI.stream()
            .noneMatch(uri -> request.getRequestURI().toLowerCase().contains(uri));

        if (isRequestToProtectedAPI
                // если пользователь еще не прошел аутентификацию (а значит объект Authentication == null в контейнере Spring)
//                && SecurityContextHolder.getContext().getAuthentication() == null
        ) {
            String jwt = request.getRequestURI().contains("update-password") // запрос на обновление пароля
                ? getJwtFromHeader(request) // получаем токен из заголовка Authorization
                : cookieUtils.getCookieAccessToken(request); // получаем токен из кука access_token

            if (Objects.nonNull(jwt)) {
                if (jwtUtils.validate(jwt)) {
                    CLogger.info("JWT validated successfully");

                    CUser user = jwtUtils.getUser(jwt);
                    UserDetailsImpl userDetails = new UserDetailsImpl(user);

                    // Создаем объект UsernamePasswordAuthenticationToken (т.е. не используем пароль и не вызываем метод authenticate,
                    // как в методе login - это уже сделано ранее и был создан jwt)
                    // Привязываем UsernamePasswordAuthenticationToken к пользователю.
                    // Добавляем объект UsernamePasswordAuthenticationToken в Spring контейнер - тем самым Spring будет видеть,
                    // что к пользователю привязан объект authentication - соответственно он успешно залогинен
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()); // пароль не нужен

                    // важно: добавляем входящий запрос в контейнер,
                    // чтобы дальше уже Spring обрабатывал запрос с учетом данных авторизации
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // вручную добавляем объект authentication в spring контейнер - тем самым пользователь успешно залогинен и spring это видит
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else { // не смогли обработать токен (возможно вышел срок действия или любая другая ошибка)
                    throw new JwtCommonException("JWT validated failed"); // пользователь не будет авторизован (т.к. jwt некорректный) и клиенту отправится ошибка
                }
            } else {
                throw new AuthenticationCredentialsNotFoundException("Token not found"); // если запрос пришел не на публичную страницу и если не найден jwt
            }
        }

        // продолжить выполнение запроса (запрос отправиться дальше в контроллер)
        filterChain.doFilter(request, response);
    }

    /**
     * Метод для получения токена из заголовка Authorization (не из кука) - в проекте используется только в одном месте.
     * Чтобы обновить пароль, пользователь из письма переходит по URL, в конце которого указан токен.
     * Этот токен считывается на клиенте и добавляется в заголовок Authorization.
     * Не рекомендуется на клиенте создавать кук и добавлять туда токен - это не безопасно, т.к. такой client-side-cookie может быть считан.
     * Поэтому токен добавляется в заголовок запроса Authorization - 1 раз и для 1 запроса.
     * Во всех остальных случаях токен создается только сервером (флаг httpOnly) и не может быть считан с помощью JS на клиенте.
     */
    private String getJwtFromHeader(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(BEARER_PREFIX)) {
            return headerAuth.substring(7); // вырезаем префикс, чтобы получить только значение токена
        }
        return null; // токен не найден
    }

}
