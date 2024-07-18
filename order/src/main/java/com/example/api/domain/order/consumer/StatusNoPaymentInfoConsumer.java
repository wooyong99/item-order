package com.example.api.domain.order.consumer;

import com.example.api.domain.order.service.OrderService;
import com.example.core.domain.order.domain.OrderStatusEnum;
import com.example.core.kafka.dto.StatusNoPaymentInfoMessage;
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
public class StatusNoPaymentInfoConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "ORDER_STATUS_NO_PAYMENT_INFO", groupId = "status-no-payment-info")
    public void orderConsume(String msg) throws IOException {
        log.info("StatusNoPaymentInfo consumer : {}", msg);

        ObjectMapper objectMapper = new ObjectMapper();
        StatusNoPaymentInfoMessage convertObj = null;
        try {
            convertObj = objectMapper.readValue(msg,
                StatusNoPaymentInfoMessage.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        orderService.updateStatus(convertObj.getMerchantUid(),
            OrderStatusEnum.PAYMENT_NO_PAYMENT_INFO);
    }

}
