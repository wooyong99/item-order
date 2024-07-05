package com.example.core.domain.user.dto;

import com.example.core.domain.order.domain.OrderStatusEnum;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderInfoResponse {

    private Long itemId;

    private String merchantUid;

    private Long price;

    private OrderStatusEnum status;

    private LocalDateTime purchaseDate;

}
