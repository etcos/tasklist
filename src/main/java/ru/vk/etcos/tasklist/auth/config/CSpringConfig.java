package ru.vk.etcos.tasklist.auth.config;

import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.web.*;

@Configuration
@EnableWebSecurity(debug = true) // указывает Spring контейнеру, чтобы находил файл конфигурации в классе
public class CSpringConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Если используется другая клиентская технология (не SpringMVC, а например Angular, React и пр.), то выключаем
        // встроенную Spring защиту от CSRF атак, иначе запросы от клиента не будут обрабатываться.
        // На время разработки проекта.
        http.csrf().disable();

        // отключаем, т.к. форма авторизации создается не на Spring технологии (например, Spring MVC + JSP), а на другой технологии
        http.formLogin().disable();

        // отключаем стандартную браузерную форму авторизации
        http.httpBasic().disable();

        // обязательное использование HTTPS
        http.requiresChannel().anyRequest().requiresSecure();

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
