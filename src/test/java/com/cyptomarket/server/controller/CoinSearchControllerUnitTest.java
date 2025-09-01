package com.cyptomarket.server.controller;

import com.cyptomarket.server.dto.CoinSearchRequestV1;
import com.cyptomarket.server.service.CoinSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CoinSearchController.class)
@ExtendWith(MockitoExtension.class)
public class CoinSearchControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CoinSearchService coinSearchService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("should return 400 when keyword is empty")
    void shouldReturnBadRequestWhenKeywordIsEmpty() throws Exception {
        //Given
        CoinSearchRequestV1 request = new CoinSearchRequestV1("", "KRW");

        //When & Then
        mockMvc.perform(post("/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 500 when service throws exception")
    void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
        // Given: 검색 요청 데이터 준비
        CoinSearchRequestV1 request = new CoinSearchRequestV1("비트코인", "KRW");

        // Mock 서비스 예외 설정
        when(coinSearchService.searchCoins(any(CoinSearchRequestV1.class)))
                .thenThrow(new RuntimeException("서버 오류"));

        // When & Then: POST 요청 테스트
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

}
