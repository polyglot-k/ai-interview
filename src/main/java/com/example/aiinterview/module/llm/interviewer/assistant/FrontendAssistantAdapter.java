package com.example.aiinterview.module.llm.interviewer.assistant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
@Component
@RequiredArgsConstructor
public class FrontendAssistantAdapter implements ReactiveAssistant{
    private final FrontendAssistant assistant;
    @Override
    public Flux<String> chat(int memoryId, String userInput) {
        return Flux.create(sink -> assistant.chat(memoryId, userInput)
                .onPartialResponse(sink::next)
                .onCompleteResponse(t -> sink.complete())
                .onError(sink::error)
                .start());
    }
}
