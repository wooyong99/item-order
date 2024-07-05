package com.example.api.user.controller;

import static com.example.core.common.response.ApiResponse.success;

import com.example.api.domain.user.service.UserServcie;
import com.example.core.common.response.ApiResponse;
import com.example.core.domain.user.domain.UserRole;
import com.example.core.domain.user.dto.UserInfoResponse;
import com.example.core.domain.user.dto.UserOrderInfoResponse;
import com.example.core.security.aop.AuthorizationRequired;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserServcie userServcie;

    @GetMapping("/users")
    @AuthorizationRequired({UserRole.GENERAL})
    public ResponseEntity<ApiResponse<UserInfoResponse>> userInfo(Principal principal) {

        UserInfoResponse userInfo = userServcie.getUserInfo(Long.parseLong(principal.getName()));

        return ResponseEntity.ok(success(userInfo));
    }

    @GetMapping("/users/orders")
    @AuthorizationRequired({UserRole.GENERAL})
    public ResponseEntity<ApiResponse<List<UserOrderInfoResponse>>> userOrderInfo(
        Principal principal) {

        List<UserOrderInfoResponse> userOrders = userServcie.getUserOrder(
            Long.parseLong(principal.getName()));

        return ResponseEntity.ok(success(userOrders));
    }

}
