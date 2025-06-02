package com.example.aiinterview.module.interview.infrastructure.repository;

import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.repository.InterviewMessageCompositeRepositoryCustom;
import com.example.aiinterview.module.interview.domain.vo.InterviewMessageWithStatus;
import com.example.aiinterview.module.interview.domain.vo.InterviewSessionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class InterviewMessageRepositoryImpl implements InterviewMessageCompositeRepositoryCustom {
    private final DatabaseClient databaseClient;

    /**
     * id, sender, message, createdAt
     */
    @Override
    public Mono<InterviewMessageWithStatus> retrieveMessageWithStatusBySessionId(Long sessionId) {
        return databaseClient.sql("select m.*, i.status from interview_message m join interview_session i on m.i_id = i.id where m.i_id = :sessionId")
                .bind("sessionId",sessionId)
                .map((row, metadata)->{
                    Long id = assertNotNull(row.get("id", Long.class), "id");
                    String userContent = row.get("user_content", String.class);
                    String llmContent = assertNotNull(row.get("llm_content", String.class), "llm_content");
                    LocalDateTime userCreatedAt = row.get("user_created_at", LocalDateTime.class);
                    LocalDateTime llmCreatedAt = assertNotNull(row.get("llm_created_at", LocalDateTime.class), "llm_created_at");
                    InterviewSessionStatus status = InterviewSessionStatus.valueOf(assertNotNull(row.get("status", String.class), "status"));

                    InterviewMessage message = createInterviewMessageUsingReflection(id, userContent, llmContent, userCreatedAt,llmCreatedAt);

                    return Tuples.of(message, status);
                })
                .all()
                .collectList()
                .map(list -> {
                    if (list.isEmpty()) return new InterviewMessageWithStatus(Collections.emptyList(), null);
                    List<InterviewMessage> messages = list.stream()
                            .map(Tuple2::getT1)
                            .toList();

                    InterviewSessionStatus status = list.get(0).getT2(); // status는 모두 동일하므로 첫 row 기준

                    return new InterviewMessageWithStatus(messages, status);
                });
    }
    private <T> T assertNotNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalStateException("Null value for required field: " + fieldName);
        }
        return value;
    }

    private InterviewMessage createInterviewMessageUsingReflection(Long id,
                                                                   String userContent,
                                                                   String llmContent,
                                                                   LocalDateTime userCreatedAt,
                                                                   LocalDateTime llmCreatedAt){
        try {

            InterviewMessage message = new InterviewMessage();

            Field idField = InterviewMessage.class.getDeclaredField("id");
            Field userContentField = InterviewMessage.class.getDeclaredField("userMessage");
            Field llmContentField = InterviewMessage.class.getDeclaredField("llmMessage"); // 필드명이 "message"라고 가정
            Field userCreatedAtField = InterviewMessage.class.getDeclaredField("userCreatedAt");
            Field llmCreatedAtField = InterviewMessage.class.getDeclaredField("llmCreatedAt");

            idField.setAccessible(true);
            userContentField.setAccessible(true);
            llmContentField.setAccessible(true);
            userCreatedAtField.setAccessible(true);
            llmCreatedAtField.setAccessible(true);

            idField.set(message, id);
            userContentField.set(message, userContent);
            llmContentField.set(message, llmContent);
            userCreatedAtField.set(message, userCreatedAt);
            llmCreatedAtField.set(message, llmCreatedAt);

            return message;
        } catch (NoSuchFieldException|IllegalAccessException e) {
            throw new RuntimeException("Failed to set ID via reflection", e);
        }
    }
}
