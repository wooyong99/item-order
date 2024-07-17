package com.example.core.kafka.producer;

import com.example.core.kafka.dto.PaymentConfirmMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentConfirmProducer {

    private final KafkaTemplate<String, PaymentConfirmMessage> kafkaTemplate;

    public void send(String merchantUid, String impUid) {
        kafkaTemplate.send("PAYMENT_CONFIRM", new PaymentConfirmMessage(merchantUid, impUid));
    }
}
