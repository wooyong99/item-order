package com.example.core.domain.order.domain;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public enum OrderStatusEnum {

    PAYMENT_PENDING(0, ((value) -> value + 1), ((value) -> value - 1)),
    PAYMENT_CONFIRM(1, ((value) -> value + 1), ((value) -> value - 1)),
    PAYMENT_SUCCESS(2, ((value) -> value + 1), ((value) -> value - 1)),
    PAYMENT_CANCLE(3, ((value) -> value + 1), ((value) -> value - 1));

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
        if (getValue() == 2) {
            log.warn("결제 완료 상태입니다.");
            return 2;
//            throw new IllegalArgumentException("결제 완료 상태입니다.");
        }
        return increaseExpression.apply(getValue());
    }

    public Integer decreaseStatus() {
        if (getValue() == 0) {
            log.warn("결제 대기 상태입니다.");
            return 0;
//            throw new IllegalArgumentException("결제 대기 상태입니다.");
        }
        return decreaseExpression.apply(getValue());
    }

}
