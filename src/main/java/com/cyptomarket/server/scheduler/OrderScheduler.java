package com.cyptomarket.server.scheduler;

import com.cyptomarket.server.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@RequiredArgsConstructor
public class OrderScheduler {
    private final OrderService orderService;

    // @Scheduled(fixedRate = 60000) // 1분 간격 (밀리초 단위)
    @Scheduled(fixedRate = 5000) // 5초 간격 (밀리초 단위)
    public void updateOrderStatuses() {
        orderService.updateOrderStatuses();
    }
}
