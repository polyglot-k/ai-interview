package com.example.aiinterview.module.interviewer.assistant;

import reactor.core.publisher.Flux;

public interface ReactiveAssistant {
    Flux<String> chat(int memoryId, String userInput);
}