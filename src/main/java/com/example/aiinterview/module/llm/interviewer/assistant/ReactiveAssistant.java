package com.example.aiinterview.module.llm.interviewer.assistant;

import reactor.core.publisher.Flux;

public interface ReactiveAssistant {
    Flux<String> chat(int memoryId, String userInput);
}