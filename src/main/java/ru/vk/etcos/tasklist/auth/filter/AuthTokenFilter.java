package ru.vk.etcos.tasklist.auth.filter;

import java.io.*;
import java.util.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;
import org.springframework.web.filter.*;
import ru.vk.etcos.tasklist.auth.exception.*;
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

    private JWTUtils jwtUtils;
    private CookieUtils cookieUtils;

    // Список адресов(URL) для которых не требуется авторизация (не будет проверяться JWT)
    // открытое API
    private List<String> permitURI = Arrays.asList(
        "test-no-auth", // тест
        "index", // отдельная главная страница?
        "register", // регистрация нового пользователя
        "login", // аутентификация (логин-пароль)
        "activate-account" // активация нового пользователя
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
            String jwt = cookieUtils.getCookieAccessToken(request);

            if (Objects.nonNull(jwt)) {
                if (jwtUtils.validate(jwt)) {

                    // TODO

                    CLogger.info("JWT validated successfully");
                } else {
                    throw new JwtCommonException("JWT validated failed");
                }
            } else {
                throw new AuthenticationCredentialsNotFoundException("Token not found");
            }
        }

        // продолжить выполнение запроса (запрос отправиться дальше в контроллер)
        filterChain.doFilter(request, response);
    }

}
