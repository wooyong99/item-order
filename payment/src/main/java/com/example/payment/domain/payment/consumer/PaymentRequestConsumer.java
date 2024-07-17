package com.example.payment.domain.payment.consumer;

import com.example.core.kafka.dto.PaymentRequestMessage;
import com.example.payment.domain.payment.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentRequestConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "PAYMENT_REQUEST", groupId = "payment_request_group")
    public void paymentRequestConsume(String paymentRequestMessage) throws IOException {
        log.info("PaymentRequest consumer : {}", paymentRequestMessage);

        ObjectMapper objectMapper = new ObjectMapper();
        PaymentRequestMessage convertObj = null;
        try {
            convertObj = objectMapper.readValue(paymentRequestMessage,
                PaymentRequestMessage.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        paymentService.validatePayment(convertObj.getItemId(), convertObj.getMerchantUid(),
            convertObj.getImpUid(),
            convertObj.getPrice());
    }
}
