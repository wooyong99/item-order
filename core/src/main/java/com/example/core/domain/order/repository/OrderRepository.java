package com.example.core.domain.order.repository;

import com.example.core.domain.order.domain.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderCustomRepository {
    Optional<Order> findByMerchantUid(String merchantUid);

    List<Order> findByUserId(Long userId);
}
