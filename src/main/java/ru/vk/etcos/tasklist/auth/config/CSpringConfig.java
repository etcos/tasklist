package ru.vk.etcos.tasklist.auth.config;

import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.web.servlet.*;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.http.*;
import org.springframework.security.web.*;
import org.springframework.security.web.session.*;
import ru.vk.etcos.tasklist.auth.exception.*;
import ru.vk.etcos.tasklist.auth.filter.*;

@Configuration
@EnableWebSecurity(debug = true) // указывает Spring контейнеру, чтобы находил файл конфигурации в классе
@EnableGlobalMethodSecurity(prePostEnabled = true) // включаем использование аннотаций pre/post в компонентах Spring (например @PreAuthorize)
@EnableAsync // включаем использование асинхронных вызовов
public class CSpringConfig {

    // перехватывает все входящие запросы (проверяет jwt если необходимо, автоматически логинит пользователя)
    // нужно зарегистрировать в filterchain
    private AuthTokenFilter authTokenFilter;

    // самый верхний фильтр, который отлавливает ошибки во всех последующих фильтрах
    private ExceptionHandlerFilter exceptionHandlerFilter;

    @Autowired
    public void setAuthTokenFilter(AuthTokenFilter authTokenFilter) {
        this.authTokenFilter = authTokenFilter;
    }

    @Autowired
    public void setExceptionHandlerFilter(ExceptionHandlerFilter exceptionHandlerFilter) {
        this.exceptionHandlerFilter = exceptionHandlerFilter;
    }

    // нужно отключить вызов AuthTokenFilter для сервлет контейнера (чтобы фильтр не вызывался два раза, а только один раз из Spring контейнера)
    @Bean
    public FilterRegistrationBean<AuthTokenFilter> registrationBean(AuthTokenFilter authTokenFilter) {
        // FilterRegistrationBean - регистратор фильтров для сервлет контейнера
        FilterRegistrationBean<AuthTokenFilter> authTokenFilterFilterRegistrationBean = new FilterRegistrationBean<>(authTokenFilter);
        authTokenFilterFilterRegistrationBean.setEnabled(false); // отключить использование фильтра для сервлет контейнера
        return authTokenFilterFilterRegistrationBean;
    }

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

        // Отключаем хранение сессии на сервере.
        // Клиент будет вызывать RESTful API сервера и передавать токен с инфой о пользователе
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // authTokenFilter - валидация JWT, до того, как запрос попадет в контейнер
        // добавляем наш фильтр в SecurityFilterChain
        http.addFilterBefore(authTokenFilter, SessionManagementFilter.class);

        // отлавливает ошибки в фильтрах и отправляет их клиенту в формате json
        // этот фильтр должен находиться перед всеми нашими фильтрами
        http.addFilterBefore(exceptionHandlerFilter, AuthTokenFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
