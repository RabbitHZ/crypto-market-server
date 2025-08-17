package com.cyptomarket.server.controller;

import com.cyptomarket.server.dto.CoinSearchRequestV1;
import com.cyptomarket.server.dto.CoinSearchResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Tag(name = "종목 API", description = "코인 검색 관련 API")
@RestController
@RequestMapping("/v1/api/coins")
public class CoinSearchController {
    @Operation(summary = "코인 검색", description = "이름, 키워드, 초성, 카테고리로 코인 검색. 대소문자 무시, 실시간 시세 제공. 빈 검색어 차단.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "코인 검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 검색 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/search")
    public ResponseEntity<List<CoinSearchResponseV1>> searchCoins(
            @Parameter(description = "검색 요청 객체") @Valid @RequestBody final CoinSearchRequestV1 request) {
        // Mock: 실제 검색 로직 대신 하드코딩된 결과 반환
        // 유효성 검사 예시: if (request.getKeyword().isEmpty()) throw new IllegalArgumentException("빈 검색어");
        CoinSearchResponseV1 btc = new CoinSearchResponseV1(
                "BTC/KRW", "BTC", 50000000, 49000000, 49500000, 51000000, 48000000, 1000);
        // 비슷한 방식으로 다른 코인 추가
        return ResponseEntity.ok(Arrays.asList(btc));
    }
}
