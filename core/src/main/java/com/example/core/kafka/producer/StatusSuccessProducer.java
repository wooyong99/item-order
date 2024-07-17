package com.example.core.kafka.producer;

import com.example.core.kafka.dto.StatusSuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatusSuccessProducer {

    private final KafkaTemplate<String, StatusSuccessMessage> kafkaTemplate;

    public void send(String merchantUid) {
        kafkaTemplate.send("ORDER_STATUS_SUCCESS", new StatusSuccessMessage(merchantUid));
    }
}
