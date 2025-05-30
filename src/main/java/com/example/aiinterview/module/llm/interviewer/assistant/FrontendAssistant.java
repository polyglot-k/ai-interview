package com.example.aiinterview.module.llm.interviewer.assistant;

import dev.langchain4j.service.*;

public interface FrontendAssistant extends ChatMemoryAccess {
    @SystemMessage(fromResource = "prompts/frontend-interview-prompt.md")
    TokenStream chat(@MemoryId int memoryId,@UserMessage String message);
}
