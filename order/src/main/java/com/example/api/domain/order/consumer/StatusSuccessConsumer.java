package com.example.api.domain.order.consumer;

import com.example.api.domain.order.service.OrderService;
import com.example.core.domain.order.domain.OrderStatusEnum;
import com.example.core.kafka.dto.PaymentConfirmMessage;
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
public class StatusSuccessConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "ORDER_STATUS_SUCCESS", groupId = "status-success")
    public void orderConsume(String orderMessage) throws IOException {
        log.info("StatusSuccess consumer : {}", orderMessage);

        ObjectMapper objectMapper = new ObjectMapper();
        PaymentConfirmMessage convertObj = null;
        try {
            convertObj = objectMapper.readValue(orderMessage,
                PaymentConfirmMessage.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        orderService.updateStatus(convertObj.getMerchantUid(), OrderStatusEnum.PAYMENT_SUCCESS);
    }

}
