package com.example.aiinterview.llm;

import com.example.aiinterview.llm.assistant.AssistantRouter;
import com.example.aiinterview.llm.assistant.ReactiveAssistant;
import com.example.aiinterview.llm.prompt.LLMPromptType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class LLMStreamer {

    private final AssistantRouter router;

    public Flux<String> stream(int memoryId, LLMPromptType type, String userInput) {
        ReactiveAssistant assistant = router.get(type);
        if (assistant == null) {
            return Flux.error(new IllegalArgumentException("Unknown prompt type: " + type));
        }
        return assistant.chat(memoryId, userInput);
    }
}