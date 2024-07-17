package com.example.api.domain.item.consumer;

import com.example.api.domain.item.service.ItemService;
import com.example.core.exception.StockNegativeException;
import com.example.core.kafka.dto.StockDecreaseMessage;
import com.example.core.kafka.producer.PaymentCancleProducer;
import com.example.core.kafka.producer.StatusCancleProducer;
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
public class StockDecreaseConsumer {

    private final ItemService itemService;
    private final PaymentCancleProducer paymentCancleProducer;
    private final StatusCancleProducer statusCancleProducer;

    @KafkaListener(topics = "STOCK_DECREASE", groupId = "stock-decrease")
    public void itemConsume(String itemMessage) throws IOException {
        log.info("StockDecrease consumer : {}", itemMessage);

        ObjectMapper objectMapper = new ObjectMapper();
        StockDecreaseMessage convertObj = null;
        try {
            convertObj = objectMapper.readValue(itemMessage,
                StockDecreaseMessage.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            itemService.decreaseStock(convertObj.getItemId(), convertObj.getMerchantUid());
        } catch (StockNegativeException e) {                            // 재고 부족 시
            log.warn("상품의 재고가 부족합니다.");
            statusCancleProducer.send(
                convertObj.getMerchantUid());          // 주문 상태 결제 취소 변경 이벤트 발행
            paymentCancleProducer.send(convertObj.getImpUid());    // 결제 취소 이벤트 발행
        }
    }
}
