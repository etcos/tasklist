package ru.vk.etcos.tasklist.auth.filter;

import java.io.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.filter.*;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    // Этот метод вызывается автоматически при каждом входящем запросе
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        // продолжить выполнение запроса (запрос отправиться дальше в контроллер)
        filterChain.doFilter(request, response);
    }
}
