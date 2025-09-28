package com.cyptomarket.server.repository;

import com.cyptomarket.server.entity.Order;
import com.cyptomarket.server.entity.enums.OrderState;
import com.cyptomarket.server.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdAndCreatedAtBetweenAndOrderState(Long userId, LocalDate startDate, LocalDate endDate, OrderState orderState, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING', 'PARTIAL_FILLED')")
    List<Order> findPendingOrPartialOrders();
}
