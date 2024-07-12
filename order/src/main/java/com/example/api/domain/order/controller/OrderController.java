package com.example.api.domain.order.controller;

import com.example.api.domain.order.service.OrderService;
import com.example.core.domain.order.dto.OrderCreateRequest;
import com.example.core.domain.user.domain.UserRole;
import com.example.core.security.aop.AuthorizationRequired;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "아이템 주문")
    @PostMapping("/items/{itemId}/orders")
    @AuthorizationRequired({UserRole.GENERAL})
    public ResponseEntity getMerchantUID(@PathVariable long itemId,
        @RequestBody OrderCreateRequest request) {

        String merchantUid = orderService.save(itemId, request);

        return ResponseEntity.ok(merchantUid);
    }
}
