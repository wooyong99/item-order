package com.example.api.domain.order.repository;

import com.example.api.domain.user.Util;
import com.example.core.db.config.QueryDslConfig;
import com.example.core.domain.item.domain.Item;
import com.example.core.domain.order.domain.Order;
import com.example.core.domain.order.repository.OrderRepository;
import com.example.core.domain.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;

@DataJpaTest
@Import({QueryDslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("OrderRepositoryTest 테스트")
@Slf4j
public class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    User user;
    Item item;

    @BeforeEach
    void setUp() {
        user = Util.createUser("email", "password", "nickname");
        item = Util.createItem(Util.randomUID(), 100L, 1000L);
    }

    @Test
    @DisplayName("Order 데이터가 저장되는지 확인합니다.")
    public void shouldPersistOrderSuccessfully() {
        // given
        Order order = Util.createOrder(user, 1000L, Util.randomUID(), item);

        // when
        orderRepository.save(order);

        // then
        Assertions.assertThat(orderRepository.findById(order.getId()).get()).isEqualTo(order);
    }

    @Test
    @DisplayName("가격이 0이하 일 때 예외를 던집니다.")
    public void shouldThrowExceptionPriceZeroOrNegative() {
        // given
        Order order = Util.createOrder(user, 0L, Util.randomUID(), item);

        // when
        Assertions.assertThatThrownBy(() -> orderRepository.save(order)).isExactlyInstanceOf(
            InvalidDataAccessApiUsageException.class);
    }
}
