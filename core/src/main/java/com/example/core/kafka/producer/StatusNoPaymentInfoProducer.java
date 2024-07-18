package com.example.core.kafka.producer;

import com.example.core.kafka.dto.StatusNoPaymentInfoMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatusNoPaymentInfoProducer {

    private final KafkaTemplate<String, StatusNoPaymentInfoMessage> kafkaTemplate;

    public void send(String merchantUid) {
        kafkaTemplate.send("ORDER_STATUS_NO_PAYMENT_INFO",
            new StatusNoPaymentInfoMessage(merchantUid));
    }

}
