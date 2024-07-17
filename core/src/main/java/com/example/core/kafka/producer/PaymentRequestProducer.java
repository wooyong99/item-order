package com.example.core.kafka.producer;

import com.example.core.kafka.dto.PaymentRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRequestProducer {

    private final KafkaTemplate<String, PaymentRequestMessage> kafkaTemplate;

    public void send(Long itemId, String merchantUid, String impUid, Long price) {
        kafkaTemplate.send("PAYMENT_REQUEST",
            new PaymentRequestMessage(itemId, merchantUid, impUid, price));
    }
}
