package com.example.aiinterview.module.llm.analysis;

import reactor.core.publisher.Mono;

public interface Analyzer<T> {
    Mono<T> analyze(String message);
}
