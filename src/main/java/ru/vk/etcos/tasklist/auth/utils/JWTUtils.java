package ru.vk.etcos.tasklist.auth.utils;

import java.util.*;

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

    // секретный ключ для создания jwt (храниться только на сервере, нельзя передавать)
    @Value("${jwt.secret}")
    private String jwtSecret;

    // длительность токена для авто.логина
    @Value("${jwt.access_token-expiration}")
    private int accessTokenExpiration;

    public String createAccessToken(CUser user) {
        // для отсчета времени от текущего момента
        Date currentDate = new Date();

        return Jwts.builder() // какие именно данные (claims) добавлять в jwt
            .setSubject(user.getId().toString()) // одно из стандартных полей jwt
            .setIssuedAt(currentDate) // время отсчета
            .setExpiration(new Date(currentDate.getTime() + accessTokenExpiration)) // срок действия access_token
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
}
