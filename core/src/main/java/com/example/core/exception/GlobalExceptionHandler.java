package com.example.core.exception;

import com.example.core.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class, InternalServerException.class})
    ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception exception) {
        log.error("message", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("서버 내부 에러"));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class,
        LoginFailedException.class,
        UserAlreadyExistsException.class})
    ResponseEntity<ApiResponse<Void>> handleBadRequestException(RuntimeException exception) {
        log.error("message", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(exception.getMessage()));

    }

    @ExceptionHandler({AuthorizationException.class})
    ResponseEntity<ApiResponse<Void>> handleJwtUnauthorizedException(RuntimeException exception) {
        log.error("message", exception);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(NotFoundUserException.class)
    ResponseEntity<ApiResponse<Void>> handleNotFoundException(RuntimeException exception) {
        log.error("message", exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(exception.getMessage()));
    }
}
