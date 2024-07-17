package com.example.core.kafka.producer;

import com.example.core.kafka.dto.StatusCancleMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatusCancleProducer {

    private final KafkaTemplate<String, StatusCancleMessage> kafkaTemplate;

    public void send(String merchantUid) {
        kafkaTemplate.send("ORDER_STATUS_CANCLE", new StatusCancleMessage(merchantUid));
    }
}
