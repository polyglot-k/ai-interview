package com.example.aiinterview.module.interviewer.assistant;

import dev.langchain4j.service.*;

public interface BackendAssistant extends ChatMemoryAccess {
    @SystemMessage(fromResource = "prompts/backend-interview-prompt.md")
    TokenStream chat(@MemoryId int memoryId,@UserMessage String message);
}
