package com.example.payment.domain.payment.consumer;

import com.example.core.kafka.dto.PaymentCancleMessage;
import com.example.payment.domain.payment.service.PaymentService;
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
public class PaymentCancleConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "PAYMENT_CANCLE", groupId = "payment_cancle_group")
    public void paymentCancleConsume(String paymentCancleMessage) throws IOException {
        log.info("PaymentCancle consumer : {}", paymentCancleMessage);

        ObjectMapper objectMapper = new ObjectMapper();
        PaymentCancleMessage convertObj = null;
        try {
            convertObj = objectMapper.readValue(paymentCancleMessage,
                PaymentCancleMessage.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        paymentService.canclePayment(convertObj.getImpUid());
    }
}
