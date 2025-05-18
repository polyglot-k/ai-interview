package com.example.aiinterview;

import com.example.aiinterview.module.interview.infrastructure.SessionStreamingStatusRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.TimeZone;
@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class InterviewWithAIApplication implements CommandLineRunner{
    private final SessionStreamingStatusRegistry registry;
    @PostConstruct
    public void initTimeZone(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        log.info("TimeZone Setting 성공 - [현재 시간] {}", LocalDateTime.now());
    }

    public static void main(String[] args) {
        SpringApplication.run(InterviewWithAIApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // registry.setStreamStatus(1L, SessionStreamingStatusRegistry.StreamingStatus.TERMINATED).block();
    }
}



