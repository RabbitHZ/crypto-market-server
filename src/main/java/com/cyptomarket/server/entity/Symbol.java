package com.cyptomarket.server.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "symbol")
public class Symbol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String symbol;

    @Column(name = "base_coin", nullable = false)
    private String baseCoin;

    @Column(name = "quote_coin", nullable = false)
    private String quoteCoin;

    @OneToMany(mappedBy = "symbol")
    private List<Order> orders;
}
