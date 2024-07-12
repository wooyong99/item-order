package com.example.api.domain.user;

import com.example.core.domain.item.domain.Item;
import com.example.core.domain.order.domain.Order;
import com.example.core.domain.user.domain.User;
import java.util.UUID;

public class Util {

    public static User createUser(String email, String password, String nickname) {
        return User.builder()
            .email(email)
            .password(password)
            .nickname(nickname)
            .build();
    }

    public static String randomUID() {
        return UUID.randomUUID().toString();
    }

    public static Order createOrder(User user, Long price, String merchantUID, Item item) {
        return Order.builder()
            .user(user)
            .price(price)
            .merchantUid(merchantUID)
            .item(item)
            .build();
    }

    public static Item createItem(String name, Long stock, Long price) {
        return Item.builder()
            .name(name)
            .stock(stock)
            .price(price)
            .build();
    }
}
