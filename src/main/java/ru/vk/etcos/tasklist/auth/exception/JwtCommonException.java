package ru.vk.etcos.tasklist.auth.exception;

import org.springframework.security.core.*;

public class JwtCommonException extends AuthenticationException {
    public JwtCommonException(String msg) {
        super(msg);
    }

    public JwtCommonException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
