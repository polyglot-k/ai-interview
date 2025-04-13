package com.example.aiinterview.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class BaseEntityWithUpdate extends BaseEntity {

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}