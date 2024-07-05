package com.example.core.exception;


public class InternalServerException extends RuntimeException {

    public InternalServerException() {
        this("서버에 이상이 발생하여 요청이 실패했습니다.");
    }

    public InternalServerException(String message) {
        super(message);
    }
}
