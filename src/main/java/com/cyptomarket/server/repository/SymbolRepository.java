package com.cyptomarket.server.repository;

import com.cyptomarket.server.entity.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SymbolRepository extends JpaRepository<Symbol, Long> {
    Optional<Symbol> findBySymbol(String symbol);

    @Query("SELECT s FROM Symbol s WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(s.symbol) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.baseCoin) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:category IS NULL OR :category = '' OR s.quoteCoin = :category)")
    List<Symbol> searchByKeywordAndCategory(@Param("keyword") String keyword, @Param("category") String category);

    @Query("SELECT s FROM Symbol s WHERE " +
            "(:chosung IS NULL OR :chosung = '' OR " +
            "LOWER(s.baseCoin) LIKE LOWER(CONCAT(:chosung, '%'))) AND " +
            "(:category IS NULL OR :category = '' OR s.quoteCoin = :category)")
    List<Symbol> searchByChosungAndCategory(@Param("chosung") String chosung, @Param("category") String category);
}
