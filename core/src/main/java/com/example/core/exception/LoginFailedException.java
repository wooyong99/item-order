package com.example.core.exception;

public class LoginFailedException extends RuntimeException {

    public LoginFailedException() {
        this("로그인 정보가 정확하지 않습니다.");
    }

    public LoginFailedException(String message) {
        super(message);
    }
}
