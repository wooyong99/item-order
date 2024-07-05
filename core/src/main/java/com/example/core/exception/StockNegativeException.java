package com.example.core.exception;

public class StockNegativeException extends RuntimeException {

    public StockNegativeException() {
        this("해당 상품의 재고가 부족합니다.");
    }

    public StockNegativeException(String message) {
        super(message);
    }

}
