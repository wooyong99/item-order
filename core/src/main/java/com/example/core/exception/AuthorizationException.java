package com.example.core.exception;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException() {
        this("접근 권한이 없습니다.");
    }

    public AuthorizationException(String message) {
        super(message);
    }
}

