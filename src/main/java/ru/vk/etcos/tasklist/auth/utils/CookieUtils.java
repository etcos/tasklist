package ru.vk.etcos.tasklist.auth.utils;

import org.apache.tomcat.util.http.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;

/*
Утилиты для работы с куками

Кук jwt создается на сервере и управляется только сервером (создается, удаляется) - "server-side cookie"
На клиенте этот кук нельзя считать с помощью JS (т.к. включен httpOnly) - для безопасности от XSS атак
Также, обязательно канал должен быть HTTPS, чтобы нельзя было дешифровать данные запросов между клиентом (браузером) и сервером
 */

@Component // добавляем в Spring контейнер, будет доступен для любого Spring компонента (контроллеры, сервисы и пр.)
public class CookieUtils {

    // имя кука, который будет хранить jwt (возьмем стандартное имя)
    private final String ACCESS_TOKEN = "access_token";

    @Value("${cookie.jwt.max-age}")
    private int cookieAccessTokenDuration;

    @Value("${cookie.domain}")
    private String cookieAccessTokenDomain;

    // Создает server-side cookie со значением jwt. Важно: этот кук сможет считать только сервер, клиент не сможет (для безопасности)
    public HttpCookie createJWTCookie(String jwt) {
        return ResponseCookie.from(ACCESS_TOKEN, jwt) // название и значение кука, если запрос пришел со стороннего сайта
            .maxAge(cookieAccessTokenDuration) // продолжительность 86400 сек = 1 сутки
            .sameSite(SameSiteCookies.STRICT.getValue()) // запрет на отправку кука
            .httpOnly(true) // кук будет доступен для считывания только на сервере
            .secure(true) // кук будет передаваться браузером на сервер только если канал защищен (https)
            .domain(cookieAccessTokenDomain) // для какого домена действует кук
            .path("/") // кук будет доступен для всех URL
            .build();

        /*
        Все настройки кука (domain, path и пр.) влияют на то, будет ли браузер отправлять их при запросе.
        Браузер сверяет URL запроса (который набрали в адресной строке или любой ajax запрос формы) с параметрами кука.
        И если есть хотя бы одно не совпадение (например domain или path) - кук отправлен не будет.
         */
    }
}
