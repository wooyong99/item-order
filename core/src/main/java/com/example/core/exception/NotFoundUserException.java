package com.example.core.exception;

public class NotFoundUserException extends RuntimeException{
    public NotFoundUserException() {
        this("존재하지 않는 사용자입니다.");
    }

    public NotFoundUserException(String message) {
        super(message);
    }

}
