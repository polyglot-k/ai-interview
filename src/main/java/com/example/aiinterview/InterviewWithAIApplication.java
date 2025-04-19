package com.example.aiinterview;

import com.example.aiinterview.module.llm.assistant.BackendAssistant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class InterviewWithAIApplication implements CommandLineRunner{
    public static void main(String[] args) {
        SpringApplication.run(InterviewWithAIApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}



