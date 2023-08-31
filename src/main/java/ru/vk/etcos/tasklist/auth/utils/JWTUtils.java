package ru.vk.etcos.tasklist.auth.utils;

import java.util.*;

import com.fasterxml.jackson.databind.*;
import io.jsonwebtoken.*;
import lombok.extern.java.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import ru.vk.etcos.tasklist.auth.entity.*;
import ru.vk.etcos.tasklist.util.*;

/*
Утилита для работы с токеном JWT (генерация, парсинг данных, валидация)
Сам jwt не шифруем, т.к. он будет передаваться по HTTPS и автоматически будет шифроваться
 */
@Component
@Log
public class JWTUtils {

    public static final String CLAIM_USER_KEY = "user";
    // секретный ключ для создания jwt (храниться только на сервере, нельзя передавать)
    @Value("${jwt.secret}")
    private String jwtSecret;

    // длительность токена для авто.логина
    @Value("${jwt.access_token-expiration}")
    private int accessTokenExpiration;

    // длительность токена для сброса пароля(чем короче, тем лучше)
    @Value("${jwt.reset-pass-expiration}")
    private int resetPassTokenExpiration;


    // создает JWT для доступа к данным
    // в user будут заполнены те поля, которые нужны аутентификации и работы в системе
    public String createAccessToken(CUser user) {
        return createToken(user, accessTokenExpiration);
    }

    // создает JWT для сброса пароля
    // в user будут заполнены те поля, которые нужны для сброса пароля
    public String createEmailResetToken(CUser user) {
        return createToken(user, resetPassTokenExpiration);
    }

    // создает JWT с нужным сроком действия
    private String createToken(CUser user, int duration) {
        // для отсчета времени от текущего момента
        Date currentDate = new Date();

        Map<String, Object> claims = new HashMap<>();
        claims.put(Claims.EXPIRATION, new Date(currentDate.getTime() + duration)); // срок действия access_token
        claims.put(Claims.ISSUED_AT, currentDate); // время отсчета
        claims.put(CLAIM_USER_KEY, user); // пользователь
        claims.put(Claims.SUBJECT, user.getId()); // системные поля sub также можно добавлять

        return Jwts.builder() // какие именно данные (claims) добавлять в jwt
            .setClaims(claims) // добавляем все claims
            .signWith(SignatureAlgorithm.HS256, jwtSecret) // алгоритм кодирования
            .compact(); // преобразовать в формат Base64
    }

    // Проверяет целостность данных
    public boolean validate(String jwt) {
        try {
            Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(jwt);
            return true;
        } catch (MalformedJwtException e) {
            CLogger.fatal("Invalid JWT", e);
        } catch (ExpiredJwtException e) {
            CLogger.fatal("JWT is expired", e);
        } catch (UnsupportedJwtException e) {
            CLogger.fatal("JWT is unsupported", e);
        } catch (IllegalArgumentException e) {
            CLogger.fatal("JWT claims is empty", e);
        }

        return false; // валидация не прошла - значит данные payload были изменены или они были подписаны не нашим секретным ключом.
    }

    // Получение поля sub из jwt
    public CUser getUser(String jwt) {
        Map map = (Map) Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(jwt)
            .getBody()
            .get(CLAIM_USER_KEY);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(map, CUser.class);
    }
}
