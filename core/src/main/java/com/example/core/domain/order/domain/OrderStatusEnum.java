package com.example.core.domain.order.domain;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum OrderStatusEnum {

    BEFORE_PAYMENT(0, ((value) -> value + 1), ((value) -> value - 1)),
    AFTER_PAYMENT(1, ((value) -> value + 1), ((value) -> value - 1));

    private static final Map<Integer, OrderStatusEnum> valueToName =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(OrderStatusEnum::getValue, Function.identity())));

    private final Integer value;

    private final Function<Integer, Integer> increaseExpression;

    private final Function<Integer, Integer> decreaseExpression;

    OrderStatusEnum(Integer value, Function<Integer, Integer> increaseExpression,
                    Function<Integer, Integer> decreaseExpression) {
        this.value = value;
        this.increaseExpression = increaseExpression;
        this.decreaseExpression = decreaseExpression;
    }

    public static OrderStatusEnum getByValue(Integer value) {
        return valueToName.get(value);
    }

    public Integer increaseStatus() {
        if (getValue() == 1) {
            throw new IllegalArgumentException();
        }
        return increaseExpression.apply(getValue());
    }

    public Integer decreaseStatus() {
        if (getValue() == 0) {
            throw new IllegalArgumentException();
        }
        return decreaseExpression.apply(getValue());
    }

}
