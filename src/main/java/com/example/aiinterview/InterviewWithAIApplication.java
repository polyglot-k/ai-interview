package com.example.aiinterview;

import com.example.aiinterview.llm.assistant.BackendAssistant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class InterviewWithAIApplication implements CommandLineRunner{
//    private  final Assistantaa backendAssistant;
    private final BackendAssistant backendAssistant;
    public static void main(String[] args) {
        SpringApplication.run(InterviewWithAIApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        backendAssistant.chat(1,"너는 누구야?").onPartialResponse(System.out::println)
//                .onCompleteResponse(System.out::println)
//                .onError(Throwable::printStackTrace)
//                .start();
//        backendAssistant.chat(1,"내 이름이 뭐야?").onPartialResponse(System.out::println)
//                .onCompleteResponse(System.out::println)
//                .onError(Throwable::printStackTrace)
//                .start();
//
//        backendAssistant.chat(2,"내 이름이 뭐야?").onPartialResponse(System.out::println)
//                .onCompleteResponse(System.out::println)
//                .onError(Throwable::printStackTrace)
//                .start();
    }
}
