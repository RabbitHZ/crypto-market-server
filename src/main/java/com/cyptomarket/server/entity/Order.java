package com.cyptomarket.server.entity;

import com.cyptomarket.server.entity.commons.BaseEntity;
import com.cyptomarket.server.entity.enums.OrderState;
import com.cyptomarket.server.entity.enums.OrderStatus;
import com.cyptomarket.server.entity.enums.OrderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@Table(name = "`order`")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "symbol_id", nullable = false)
    private Symbol symbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_state", nullable = false)
    private OrderState orderState;

    private double price;

    @Column(nullable = false)
    private double quantity;

    @Column(name = "executed_quantity", nullable = false)
    private double executedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "buyOrder")
    private List<Trade> buyTrades;

    @OneToMany(mappedBy = "sellOrder")
    private List<Trade> sellTrades;

    public static Order createOrder(User user, Symbol symbol, OrderType orderType, OrderState orderState, double price, double quantity) {
        return Order.builder()
                .user(user)
                .symbol(symbol)
                .orderType(orderType)
                .orderState(orderState)
                .price(price)
                .quantity(quantity)
                .status(OrderStatus.PENDING)
                .executedQuantity(0.0)
                .buyTrades(new ArrayList<>())
                .sellTrades(new ArrayList<>())
                .build();
    }

    public static Order updateOrder(Symbol symbol, OrderType orderType, OrderState orderState, double price, double quantity) {
        return Order.builder()
                .symbol(symbol)
                .orderType(orderType)
                .orderState(orderState)
                .price(price)
                .quantity(quantity)
                .build();
    }

    public static Order cancelOrder(OrderStatus orderStatus) {
        return Order.builder()
                .status(orderStatus)
                .build();
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }
}
