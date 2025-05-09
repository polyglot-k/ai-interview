package com.example.aiinterview;

import com.example.aiinterview.module.llm.analysis.InterviewSessionAnalyzer;
import com.example.aiinterview.module.llm.analysis.dto.InterviewSessionResult;
import com.example.aiinterview.module.llm.analysis.dto.PartialEvaluation;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class InterviewWithAIApplication implements CommandLineRunner{
    private final InterviewSessionAnalyzer analyzer;

    public static void main(String[] args) {
        SpringApplication.run(InterviewWithAIApplication.class, args);
    }
    @Override
    public void  run(String... args) throws Exception {
        InterviewSessionResult result = analyzer.analyze(1L).block();
        assert result != null;
        for(PartialEvaluation e:result.evaluations()){
            System.out.println(e.feedback());
            System.out.println(e.interviewMessageId());
            System.out.println(e.score());
            System.out.println("===========================");
        }
        System.exit(1);
    }
}



