package com.example.core.domain.order.dto;

import com.example.core.domain.order.domain.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OrderStatusResponse {

    private OrderStatusEnum status;

    private String msg;

}
