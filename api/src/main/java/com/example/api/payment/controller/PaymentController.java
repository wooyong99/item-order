package com.example.api.payment.controller;

import com.example.api.domain.payment.service.PaymentService;
import com.example.core.domain.payment.dto.PaymentValidateRequest;
import com.example.core.domain.user.domain.UserRole;
import com.example.core.security.aop.AuthorizationRequired;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payment/{merchantUid}")
    @Operation(summary = "결제 확인", description = "주문번호와 PG사로부터 받은 결제내역, 결제금액에 대한 유효성 검사를 진행합니다.")
    @AuthorizationRequired({UserRole.GENERAL})
    public ResponseEntity validateMerchantUID(@PathVariable String merchantUid,
        @RequestBody PaymentValidateRequest request) {
        paymentService.validatePayment(merchantUid, request);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "결제 취소", description = "결제 대행사로부터 결제를 취소합니다.")
    @DeleteMapping("/payment/{merchantUid}")
    @AuthorizationRequired({UserRole.GENERAL})
    public ResponseEntity refundPayment(@PathVariable String merchantUid) {

        paymentService.canclePayment(merchantUid);
        return null;
    }
}
