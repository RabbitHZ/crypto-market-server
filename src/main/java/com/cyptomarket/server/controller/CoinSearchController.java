package com.cyptomarket.server.controller;

import com.cyptomarket.server.dto.CoinSearchRequestV1;
import com.cyptomarket.server.dto.CoinSearchResponseV1;
import com.cyptomarket.server.service.CoinSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Tag(name = "종목 API", description = "코인 검색 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/coins")
public class CoinSearchController {
    private final CoinSearchService coinSearchService;

    @Operation(summary = "코인 검색", description = "이름, 키워드, 초성, 카테고리로 코인 검색. 대소문자 무시, 실시간 시세 제공. 빈 검색어 차단.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "코인 검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 검색 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/search")
    public ResponseEntity<List<CoinSearchResponseV1>> searchCoins(
            @Parameter(description = "검색 요청 객체") @Valid @RequestBody final CoinSearchRequestV1 request) {
        try {
            List<CoinSearchResponseV1> result = coinSearchService.searchCoins(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }
}
