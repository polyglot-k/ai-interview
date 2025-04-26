package com.example.aiinterview.module.interviewer.prompt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Slf4j
public class LLMPromptLoader {
    private final Map<String, String> files;

    public LLMPromptLoader(LLMPromptProperties properties) {
        this.files = properties.getFiles();
        validateFiles();
    }

    public Map<LLMPromptType, String> loadPrompts() {
        Map<LLMPromptType, String> promptMap = new HashMap<>();

        assert files != null;
        for (Map.Entry<String, String> entry : files.entrySet()) {
            LLMPromptType key = LLMPromptType.from(entry.getKey());
            String filename = entry.getValue();

            log.info("파일 처리 중 - Key: " + key + ", Filename: " + filename);

            try {
                String content = readFromResource("prompts/" + filename);
                promptMap.put(key, content.trim());

                log.info("파일 내용 - Key: " + key + ", Content: " + content.trim());
            } catch (Exception e) {
                log.error("⚠️ 프롬프트 로딩 실패: " + filename + " → " + e.getMessage());
            }
        }

        return promptMap;
    }

    private void validateFiles(){
        if (files == null) {
            log.error("⚠️ 파일 목록이 null입니다.");
        } else {
            log.info("파일 목록: " + files);
        }
    }
    private String readFromResource(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().reduce("", (acc, line) -> acc + line + "\n");
        }
    }
}
