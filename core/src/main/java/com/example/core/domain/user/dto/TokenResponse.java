package com.example.core.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {

    private String token;

    private String refreshToken;
}
