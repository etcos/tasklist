package ru.vk.etcos.tasklist.auth.exception;

import java.io.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.filter.*;

// Перехватывает ошибки всех фильтров, которые выполняются после текущего
// оборачивает ошибки в формат JSON и отправляет клиенту
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
            ServletException, IOException {

        try {
            filterChain.doFilter(request, response); // вызов следующего по цепочке фильтра
        } catch (RuntimeException e) {
            // создать JSON и отправить название класса ошибки
            JsonException jsonException = new JsonException(e.getClass().getSimpleName());

            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // статус: не авторизован для данного действия
            response.getWriter().write(convertObjectToJson(jsonException)); // в ответе записываем JSON с классом ошибки
        }
    }

    // Метод для преобразования объекта в JSON
    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        // формируем json
        return new ObjectMapper().writeValueAsString(object);
    }
}
