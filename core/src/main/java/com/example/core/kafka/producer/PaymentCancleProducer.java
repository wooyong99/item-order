package com.example.core.kafka.producer;

import com.example.core.kafka.dto.PaymentCancleMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCancleProducer {

    private final KafkaTemplate<String, PaymentCancleMessage> kafkaTemplate;

    public void send(String merchantUid) {
        kafkaTemplate.send("PAYMENT_CANCLE", new PaymentCancleMessage(merchantUid));
    }
}
