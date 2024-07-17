package com.example.api.domain.user.controller;

import com.example.api.domain.user.service.AuthService;
import com.example.core.common.response.ApiResponse;
import com.example.core.domain.user.dto.LoginRequest;
import com.example.core.domain.user.dto.SignupRequest;
import com.example.core.domain.user.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/users/signup")
    @Operation(summary = "회원가입")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/login")
    @Operation(summary = "로그인")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok().body(ApiResponse.success(tokenResponse));
    }
}
