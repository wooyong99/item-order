package com.example.api.domain.item.repository;

import com.example.core.domain.item.domain.Item;
import com.example.core.domain.item.repository.ItemRepository;
import com.example.core.domain.order.domain.Order;
import com.example.core.domain.order.repository.OrderRepository;
import com.example.core.domain.user.domain.User;
import com.example.core.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Rollback(false)
public class OrderSave {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;
    Random rnd = new Random();

    @Test
    @Transactional
    void test() {
        User user = userRepository.findById(1L).get();
        Item item = itemRepository.findById(18000L).get();

        List<Order> list = new ArrayList<>();
        for (int i = 0; i < 20000; i++) {
            Order build = Order.builder().price(1000L).item(item).user(user)
                .merchantUid(createMerchantUid()).build();
            list.add(build);
        }
        orderRepository.saveAll(list);

    }

    private String createMerchantUid() {
        StringBuilder merchantUid = new StringBuilder("");
        for (int i = 0; i < 6; i++) {
            merchantUid.append(rnd.nextInt(0, 10));
        }
        return merchantUid.toString();
    }

}
