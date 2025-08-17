package com.cyptomarket.server.controller;

import com.cyptomarket.server.dto.OrderHistoryV1;
import com.cyptomarket.server.dto.OrderQueryRequestV1;
import com.cyptomarket.server.dto.OrderRequestV1;
import com.cyptomarket.server.dto.OrderResponseV1;
import com.cyptomarket.server.entity.enums.OrderState;
import com.cyptomarket.server.entity.enums.OrderStatus;
import com.cyptomarket.server.entity.enums.OrderType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
@Tag(name = "주문 API", description = "코인 매매 및 주문 조회 API")
@RestController
@RequestMapping("/v1/api/orders")
public class OrderController {

    // userId를 추출하는 유틸리티 메서드
    private Long getAuthenticatedUserId() {
        // 실제 구현에서는 인증 객체에서 userId를 추출
        // 여기서는 Mock으로 하드코딩된 userId 반환
        return 1L; // Mock userId
    }
    @Operation(summary = "주문 생성 (매수/매도)", description = "매수 또는 매도 주문 처리. 시장가/지정가 지원, 잔액/수량 검증, 최소 금액(KRW:5000~1e9, USDT:5~1e6, BTC:0.00005~10).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    public ResponseEntity<OrderResponseV1> createOrder(
            @Parameter(description = "주문 요청 객체") @Valid @RequestBody final OrderRequestV1 request) {
        // 인증된 사용자의 userId 추출
        Long userId = getAuthenticatedUserId();
        // 유효성 검사 예시: if (request.getQuantity() <= 0) throw new IllegalArgumentException("음수 수량");
        OrderResponseV1 response = new OrderResponseV1(1L, OrderState.BUY, OrderStatus.PENDING, 0, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "주문 상태 조회", description = "과거/현재 주문 조회. 최근 50건, 페이지네이션, 필터링(날짜 범위 최대 5년, 상태). 인증 검증 필요.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 조회 파라미터"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping
    public ResponseEntity<List<OrderHistoryV1>> getOrderHistory(
            @Parameter(description = "조회 요청 객체") @Valid @RequestBody final OrderQueryRequestV1 request) {
        // 인증된 사용자의 userId 추출
        Long userId = getAuthenticatedUserId();

        // 필터링 예시: 날짜 범위 검사 (현재일 -5년 ~ 현재)
        OrderHistoryV1 order = new OrderHistoryV1(
                1L, "BTC/KRW", OrderType.LIMIT, OrderState.BUY, 50000000, 0.1, 0.05, OrderStatus.PARTIAL_FILLED, "2025-08-03", userId);
        return ResponseEntity.ok(Arrays.asList(order));
    }

    @Operation(summary = "주문 수정", description = "체결 대기 중인 주문의 가격 또는 수량 수정. 상태 확인 후 처리.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "주문 ID를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponseV1> updateOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId,
            @Parameter(description = "주문 수정 요청 객체") @Valid @RequestBody final OrderRequestV1 request) {
        // 인증된 사용자의 userId 추출
        Long userId = getAuthenticatedUserId();

        // 유효성 검사 예시: if (request.quantity() <= 0) throw new IllegalArgumentException("음수 수량");
        OrderResponseV1 response = new OrderResponseV1(orderId, request.orderState(), OrderStatus.PENDING,0, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "주문 취소", description = "체결 대기 중인 주문 취소. 상태 확인 후 처리.")
    @DeleteMapping("/{orderId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 취소 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "주문 ID를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<String> cancelOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId) {
        // 인증된 사용자의 userId 추출
        Long userId = getAuthenticatedUserId();

        // 상태 확인 예시: if (not cancellable) throw new IllegalStateException();
        return ResponseEntity.ok("주문 취소 완료");
    }
}
