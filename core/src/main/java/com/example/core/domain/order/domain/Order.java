package com.example.core.domain.order.domain;

import com.example.core.domain.common.BaseEntity;
import com.example.core.domain.item.domain.Item;
import com.example.core.domain.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
@ToString
public class Order extends BaseEntity {

    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    User user;

    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    Item item;

    @Convert(converter = OrderStatusEnumConverter.class)
    private OrderStatusEnum status = OrderStatusEnum.BEFORE_PAYMENT;

    private Long price;

    private String merchantUid;

    private String impUid;

    @Builder
    public Order(User user, Item item, Long price, String merchantUid) {
        setUser(user);
        setItem(item);
        this.price = price;
        this.merchantUid = merchantUid;
    }

    private void setItem(Item item) {
        if (this.item != null) {
            this.item.getOrders().remove(this);
        }
        this.item = item;
        this.item.getOrders().add(this);
    }

    private void setUser(User user) {
        if (this.user != null) {
            this.user.getOrders().remove(this);
        }
        this.user = user;
        this.user.getOrders().add(this);
    }

    public void increaseStatus() {
        Integer increaseStausValue = this.status.increaseStatus();
        this.status = OrderStatusEnum.getByValue(increaseStausValue);
    }

    public void decreaseStatus() {
        Integer decreaseStatusValue = this.status.decreaseStatus();
        this.status = OrderStatusEnum.getByValue(decreaseStatusValue);

    }
}
