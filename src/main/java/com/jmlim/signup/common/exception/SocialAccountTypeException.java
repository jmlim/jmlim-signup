package com.jmlim.signup.common.exception;

import org.springframework.security.core.AuthenticationException;

public class SocialAccountTypeException extends AuthenticationException {
    public SocialAccountTypeException(String msg) {
        super(msg);
    }

    public SocialAccountTypeException(String msg, Throwable t) {
        super(msg, t);
    }
}