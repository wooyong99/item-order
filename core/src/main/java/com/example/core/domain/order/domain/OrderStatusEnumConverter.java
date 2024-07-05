package com.example.core.domain.order.domain;

import jakarta.persistence.AttributeConverter;

public class OrderStatusEnumConverter implements AttributeConverter<OrderStatusEnum, Integer> {
    @Override
    public Integer convertToDatabaseColumn(OrderStatusEnum orderStatusEnum) {
        return orderStatusEnum.getValue();
    }

    @Override
    public OrderStatusEnum convertToEntityAttribute(Integer value) {
        return OrderStatusEnum.getByValue(value);
    }
}
