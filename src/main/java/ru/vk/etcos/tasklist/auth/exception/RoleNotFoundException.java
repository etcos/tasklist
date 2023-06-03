package ru.vk.etcos.tasklist.auth.exception;

import org.springframework.security.core.*;

public class RoleNotFoundException extends AuthenticationException {
    public RoleNotFoundException(String msg) {
        super(msg);
    }

    public RoleNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
