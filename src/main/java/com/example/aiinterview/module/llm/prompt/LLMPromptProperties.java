package com.example.aiinterview.module.llm.prompt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "llm.prompts")
public class LLMPromptProperties {
    private Map<String, String> files;
}