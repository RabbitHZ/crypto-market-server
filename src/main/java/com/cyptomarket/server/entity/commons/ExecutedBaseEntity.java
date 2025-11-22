package com.cyptomarket.server.entity.commons;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class ExecutedBaseEntity extends BaseEntity {

    @Column(name = "executed_at", updatable = false, insertable = false)
    private LocalDateTime executedAt;
}
