package ru.vk.etcos.tasklist.auth.exception;

import org.springframework.security.core.*;

// AuthenticationException - нужен для глобальной обработки всех ошибок аутентификации
public class UsernameExistsException extends AuthenticationException {
    public UsernameExistsException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UsernameExistsException(String msg) {
        super(msg);
    }
}
