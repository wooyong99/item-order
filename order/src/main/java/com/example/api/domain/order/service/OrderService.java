package com.example.api.domain.order.service;

import com.example.core.domain.item.domain.Item;
import com.example.core.domain.item.repository.ItemRepository;
import com.example.core.domain.order.domain.Order;
import com.example.core.domain.order.dto.OrderCreateRequest;
import com.example.core.domain.order.repository.OrderRepository;
import com.example.core.domain.user.domain.User;
import com.example.core.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final int MERCHANTUI_LENGTH = 6;
    private static final Random rnd = new Random();
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

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
