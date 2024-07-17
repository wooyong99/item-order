package com.example.payment.domain.payment.service;

import com.example.core.domain.payment.dto.PaymentValidateRequest;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {

    void validatePayment(String merchantUid, PaymentValidateRequest request);

    void validatePayment(Long itemId, String merchantUid, String impUid, Long price);

    void canclePayment(String merchantUid);

}
