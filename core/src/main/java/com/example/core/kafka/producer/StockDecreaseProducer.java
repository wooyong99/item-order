package com.example.core.kafka.producer;

import com.example.core.kafka.dto.StockDecreaseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockDecreaseProducer {

    private final KafkaTemplate<String, StockDecreaseMessage> kafkaTemplate;

    public void send(Long itemId, String merchantUid, String impUid) {
        kafkaTemplate.send("STOCK_DECREASE", new StockDecreaseMessage(itemId, merchantUid, impUid));
    }

}
