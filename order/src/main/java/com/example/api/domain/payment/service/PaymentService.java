package com.example.api.domain.payment.service;

import com.example.core.domain.payment.dto.PaymentValidateRequest;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {

    void validatePayment(String merchantUid, PaymentValidateRequest request);

    void canclePayment(String merchantUid);

}
