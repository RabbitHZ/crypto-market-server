package com.cyptomarket.server.entity;
import com.cyptomarket.server.entity.commons.BaseEntity;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;
}
