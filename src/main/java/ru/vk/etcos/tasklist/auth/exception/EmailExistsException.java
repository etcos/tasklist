package ru.vk.etcos.tasklist.auth.exception;

import org.springframework.security.core.*;

// AuthenticationException - нужен для глобальной обработки всех ошибок аутентификации
public class EmailExistsException extends AuthenticationException {
    public EmailExistsException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public EmailExistsException(String msg) {
        super(msg);
    }
}
