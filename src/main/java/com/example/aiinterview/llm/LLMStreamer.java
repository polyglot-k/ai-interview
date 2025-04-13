package com.example.aiinterview.llm;

import com.example.aiinterview.llm.prompt.LLMPromptFactory;
import com.example.aiinterview.llm.prompt.LLMPromptType;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LLMStreamer {
    private final LLMPromptFactory llmPromptFactory;
    private final StreamingChatLanguageModel geminiModel;
    public Flux<String> stream(String userInput) {
        return Flux.create(sink -> {
            geminiModel.chat(
                    List.of(
                            new SystemMessage(llmPromptFactory.getPrompt(LLMPromptType.BACKEND)),
                            new UserMessage(userInput)
                    ),
                    new LLMStreamingResponseHandler(sink)
            );
        });
    }
}
