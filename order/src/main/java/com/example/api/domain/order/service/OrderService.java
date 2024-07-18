package com.example.api.domain.order.service;

import com.example.core.domain.item.domain.Item;
import com.example.core.domain.item.repository.ItemRepository;
import com.example.core.domain.order.domain.Order;
import com.example.core.domain.order.domain.OrderStatusEnum;
import com.example.core.domain.order.dto.OrderCreateRequest;
import com.example.core.domain.order.dto.OrderStatusResponse;
import com.example.core.domain.order.repository.OrderRepository;
import com.example.core.domain.payment.dto.PaymentValidateRequest;
import com.example.core.domain.user.domain.User;
import com.example.core.domain.user.repository.UserRepository;
import com.example.core.kafka.producer.PaymentCancleProducer;
import com.example.core.kafka.producer.PaymentRequestProducer;
import com.example.core.kafka.producer.StockDecreaseProducer;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final int MERCHANTUI_LENGTH = 6;
    private static final Random rnd = new Random();
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final StockDecreaseProducer stockDecreaseProducer;
    private final PaymentRequestProducer paymentRequestProducer;
    private final PaymentCancleProducer paymentCancleProducer;

    // 주문 생성
    @Transactional
    public String save(long itemId, OrderCreateRequest request) {
        String merchantUid = createMerchantUid();

        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        Order order = Order.builder()
            .user(user)
            .item(item)
            .price(request.getPrice())
            .merchantUid(merchantUid)
            .build();

        validateStock(item.getStock());

        Order saveOrder = orderRepository.save(order);
        return saveOrder.getMerchantUid();
    }

    @Transactional
    public OrderStatusResponse validateMerchantUid(String merchantUId,
        PaymentValidateRequest request) {
        Order order = null;
        try {
            order = orderRepository.findByMerchantUid(merchantUId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문번호입니다."));
        } catch (IllegalArgumentException e) {
            paymentCancleProducer.send(request.getImpUid());
            return convertOrderStatusResponse(OrderStatusEnum.PAYMENT_NO_PAYMENT_INFO);
        }
        if (order.getStatus() == OrderStatusEnum.PAYMENT_PENDING) {
            order.updateStatus(OrderStatusEnum.PAYMENT_CONFIRM);
            orderRepository.save(order);

            paymentRequestProducer.send(order.getItem().getId(), merchantUId, request.getImpUid(),
                request.getPrice());
        }
        return convertOrderStatusResponse(order.getStatus());
    }

    private OrderStatusResponse convertOrderStatusResponse(OrderStatusEnum status) {
        if (status.getValue() == 2) {
            return new OrderStatusResponse(status, "결제 성공입니다.");
        }
        if (status.getValue() == 3) {
            return new OrderStatusResponse(status, "재고가 부족합니다.");
        }
        if (status.getValue() == 4) {
            return new OrderStatusResponse(status, "주문 내역이 없습니다.");
        }
        return new OrderStatusResponse(status, "결제 진행 중 입니다.");
    }

    @Transactional
    public void updateStatus(String merchantUid, OrderStatusEnum status) {
        Order order = orderRepository.findByMerchantUid(merchantUid)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문번호입니다."));
        order.updateStatus(status);

        orderRepository.save(order);
    }

    @Transactional
    public void updateStatus(String merchantUid, String impUid, OrderStatusEnum status) {
        Order order = orderRepository.findByMerchantUid(merchantUid)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문번호입니다."));

        order.updateStatus(status);
        order.setImpUid(impUid);

        stockDecreaseProducer.send(order.getItem().getId(), merchantUid, impUid);
        orderRepository.save(order);
    }

    private void validateStock(Long stock) {
        if (stock < 1) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
    }

    private String createMerchantUid() {
        StringBuilder merchantUid = new StringBuilder("");
        for (int i = 0; i < MERCHANTUI_LENGTH; i++) {
            merchantUid.append(rnd.nextInt(0, 10));
        }
        return merchantUid.toString();
    }
}
