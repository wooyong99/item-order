package com.example.api.domain.user.service;

import com.example.core.domain.order.domain.Order;
import com.example.core.domain.order.repository.OrderRepository;
import com.example.core.domain.user.domain.User;
import com.example.core.domain.user.dto.UserInfoResponse;
import com.example.core.domain.user.dto.UserOrderInfoResponse;
import com.example.core.domain.user.repository.UserRepository;
import com.example.core.exception.NotFoundUserException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServcie {

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundUserException("존재하지 않는 사용자입니다."));

        return toUserInfoResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserOrderInfoResponse> getUserOrder(Long userId) {
        List<UserOrderInfoResponse> collect = orderRepository.findByUserId(userId).stream()
            .map(order -> toUserOrderInfoResponse(order)).collect(Collectors.toList());
        return collect;
    }

    private UserOrderInfoResponse toUserOrderInfoResponse(Order order) {
        return UserOrderInfoResponse.builder()
            .itemId(order.getItem().getId())
            .merchantUid(order.getMerchantUid())
            .price(order.getPrice())
            .status(order.getStatus())
            .purchaseDate(order.getCreatedAt())
            .build();
    }

    private UserInfoResponse toUserInfoResponse(User user) {
        return UserInfoResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .createdAt(user.getCreatedAt())
            .build();
    }


}
