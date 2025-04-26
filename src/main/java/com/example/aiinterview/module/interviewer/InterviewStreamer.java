package com.example.aiinterview.module.interviewer;

import com.example.aiinterview.module.interviewer.assistant.AssistantRouter;
import com.example.aiinterview.module.interviewer.assistant.ReactiveAssistant;
import com.example.aiinterview.module.interviewer.prompt.LLMPromptType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class InterviewStreamer {

    private final AssistantRouter router;

    public Flux<String> stream(int memoryId, LLMPromptType type, String userInput) {
        ReactiveAssistant assistant = router.get(type);
        if (assistant == null) {
            return Flux.error(new IllegalArgumentException("Unknown prompt type: " + type));
        }
        return assistant.chat(memoryId, userInput+"까지가 내 답변이고, 다음 질문 만들어줘");
    }
}