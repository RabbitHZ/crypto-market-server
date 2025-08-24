package com.cyptomarket.server.repository;

import com.cyptomarket.server.entity.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface SymbolRepository extends JpaRepository<Symbol, Long> {
    boolean existsBySymbol(String symbol);
    Optional<Symbol> findBySymbol(String symbol);
}
