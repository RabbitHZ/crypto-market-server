package com.cyptomarket.server.service;

import com.cyptomarket.server.dto.CoinSearchRequestV1;
import com.cyptomarket.server.dto.CoinSearchResponseV1;
import com.cyptomarket.server.entity.Symbol;
import com.cyptomarket.server.repository.SymbolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CoinSearchServiceUnitTest {
    @InjectMocks
    private CoinSearchService coinSearchService;
    @Mock
    private SymbolRepository symbolRepository;

    private List<Symbol> mockSymbols;

    @BeforeEach
    void setUp(){
        Symbol btcKrw = new Symbol("BTC/KRW", "Bitcoin", "KRW");
        Symbol ethKrw = new Symbol("ETH/KRW", "Ethereum", "KRW");
        Symbol btcUsdt = new Symbol("BTC/USDT", "Bitcoin", "USDT");
        mockSymbols = Arrays.asList(btcKrw, ethKrw, btcUsdt);
    }

    @Test
    @DisplayName("should return coin list when searching by keyword")
    void shouldReturnCoinListWhenSearchingByKeyword() {
        //Given
        CoinSearchRequestV1 request = new CoinSearchRequestV1("Bitcoin", "KRW");
        when(symbolRepository.searchByKeywordAndCategory("Bitcoin", "KRW"))
                .thenReturn(mockSymbols.stream()
                        .filter(s -> s.getBaseCoin().equals("Bitcoin") && s.getQuoteCoin().equals("KRW")).toList());

        //When
        List<CoinSearchResponseV1> result = coinSearchService.searchCoins(request);

        //Then
        assertEquals(1, result.size());
        assertEquals("Bitcoin", result.get(0).name());
        assertEquals("BTC/KRW", result.get(0).symbol());
        assertEquals(50000.0, result.get(0).price());
        assertEquals("KRW", result.get(0).category());
    }

    @Test
    @DisplayName("shoud return coin list when searching by chosung")
    void shouldReturnCoinListWhenSearchingByChosung() {
        //Given
        CoinSearchRequestV1 requestV1 = new CoinSearchRequestV1("ㅂ", "KRW");
        when(symbolRepository.searchByChosungAndCategory("ㅂ", "KRW"))
                .thenReturn(mockSymbols.stream()
                        .filter(s -> s.getBaseCoin().startsWith("Bitcoin") && s.getQuoteCoin().equals("KRW"))
                        .toList());

        //When
        List<CoinSearchResponseV1> result = coinSearchService.searchCoins(requestV1);

        //Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bitcoin", result.get(0).name());
        assertEquals("BTC/KRW", result.get(0).symbol());
        assertEquals("KRW", result.get(0).category());
    }

    @Test
    @DisplayName("should return empty list when no coins match")
    void shouldReturnEmptyListWhenNoCoinsMatch() {
        //Given
        CoinSearchRequestV1 request = new CoinSearchRequestV1("XRP", "BTC");
        when(symbolRepository.searchByKeywordAndCategory("XRP", "BTC"))
                .thenReturn(Collections.emptyList());

        //When
        List<CoinSearchResponseV1> result = coinSearchService.searchCoins(request);

        //Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should return empty list when keyword is null")
    void shouldReturnEmptyListWhenKeywordIsNull() {
        //Given
        CoinSearchRequestV1 request = new CoinSearchRequestV1(null, "KRW");
        when(symbolRepository.searchByKeywordAndCategory(null, "KRW"))
                .thenReturn(Collections.emptyList());

        //When
        List<CoinSearchResponseV1> result = coinSearchService.searchCoins(request);

        //Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should return empty list when keyword is empty string")
    void shouldReturnEmptyListWhenKeywordIsEmptyString() {
        //Given
        CoinSearchRequestV1 request = new CoinSearchRequestV1("", "KRW");
        when(symbolRepository.searchByKeywordAndCategory("", "KRW"))
                .thenReturn(Collections.emptyList());

        //When
        List<CoinSearchResponseV1> result = coinSearchService.searchCoins(request);

        //Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should return empty list when keyword is blank")
    void shouldReturnEmptyListWhenKeywordIsBlank() {
        //Given
        CoinSearchRequestV1 request = new CoinSearchRequestV1("   ", "KRW");
        when(symbolRepository.searchByKeywordAndCategory("   ", "KRW"))
                .thenReturn(Collections.emptyList());

        //When
        List<CoinSearchResponseV1> result = coinSearchService.searchCoins(request);

        //Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should return empty list when category is null")
    void shouldReturnEmptyListWhenCategoryIsNull() {
        //Given
        CoinSearchRequestV1 request = new CoinSearchRequestV1("Bitcoin", null);
        when(symbolRepository.searchByKeywordAndCategory("Bitcoin", null))
                .thenReturn(Collections.emptyList());

        //When
        List<CoinSearchResponseV1> result = coinSearchService.searchCoins(request);

        //Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
