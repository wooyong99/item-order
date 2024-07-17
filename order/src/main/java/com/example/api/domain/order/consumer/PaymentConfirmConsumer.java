package com.example.api.domain.order.consumer;

import com.example.api.domain.order.service.OrderService;
import com.example.core.domain.order.domain.OrderStatusEnum;
import com.example.core.kafka.dto.PaymentConfirmMessage;
import com.example.core.kafka.producer.PaymentCancleProducer;
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
public class PaymentConfirmConsumer {

    private final OrderService orderService;

    private final PaymentCancleProducer paymentCancleProducer;

    @KafkaListener(topics = "PAYMENT_CONFIRM", groupId = "payment-confirm")
    public void orderConsume(String orderMessage) throws IOException {
        log.info("Order consumer : {}", orderMessage);

        ObjectMapper objectMapper = new ObjectMapper();
        PaymentConfirmMessage convertObj = null;
        try {
            convertObj = objectMapper.readValue(orderMessage,
                PaymentConfirmMessage.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            orderService.updateStatus(convertObj.getMerchantUid(), convertObj.getImpUid(),
                OrderStatusEnum.PAYMENT_CONFIRM);
        } catch (IllegalArgumentException e) {                  // 존재하지 않는 주문 번호 일 경우
            log.warn("존재하지 않는 주문 번호입니다.");
            paymentCancleProducer.send(convertObj.getImpUid()); // 결제 취소(환불) 이벤트 발행
        }
    }

}
