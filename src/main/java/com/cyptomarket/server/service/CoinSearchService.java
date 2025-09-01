package com.cyptomarket.server.service;

import com.cyptomarket.server.dto.CoinSearchRequestV1;
import com.cyptomarket.server.dto.CoinSearchResponseV1;
import com.cyptomarket.server.entity.Symbol;
import com.cyptomarket.server.repository.SymbolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoinSearchService {

    private final SymbolRepository symbolRepository;

    @Transactional(readOnly = true)
    public List<CoinSearchResponseV1> searchCoins(CoinSearchRequestV1 request) {

        boolean isChosung = request.keyword().matches("^[ㄱ-ㅎ]+$");

        List<Symbol> symbols;
        if (isChosung) {
            symbols = symbolRepository.searchByChosungAndCategory(request.keyword(), request.category());
        } else {
            symbols = symbolRepository.searchByKeywordAndCategory(request.keyword(), request.category());
        }

        return symbols.stream()
                .map(symbol -> new CoinSearchResponseV1(
                        symbol.getBaseCoin(),
                        symbol.getSymbol(),
                        50000.0, // 실시간 시세는 더미 값으로 가정
                        symbol.getQuoteCoin()))
                .collect(Collectors.toList());
    }
}
