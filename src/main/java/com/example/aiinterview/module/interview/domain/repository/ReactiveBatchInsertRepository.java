package com.example.aiinterview.module.interview.domain.repository;

import reactor.core.publisher.Flux;

import java.util.List;

public interface ReactiveBatchInsertRepository<T,ID> {
    Flux<T> batchInsert(List<T> entities);
}
