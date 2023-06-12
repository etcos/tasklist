package ru.vk.etcos.tasklist.auth.exception;

import org.springframework.security.core.*;

public class UserAlreadyActivatedException extends AuthenticationException {
    public UserAlreadyActivatedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UserAlreadyActivatedException(String msg) {
        super(msg);
    }
}
