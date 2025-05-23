package com.example.aiinterview.module.interview.infrastructure.repository;

import com.example.aiinterview.module.interview.domain.repository.InterviewMessageCompositeRepositoryCustom;
import com.example.aiinterview.module.interview.domain.vo.InterviewMessageWithStatus;
import com.example.aiinterview.module.interview.domain.entity.InterviewMessage;
import com.example.aiinterview.module.interview.domain.vo.InterviewSender;
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
                    InterviewSender sender = InterviewSender.valueOf(assertNotNull(row.get("sender", String.class), "sender"));
                    String content = assertNotNull(row.get("content", String.class), "content");
                    LocalDateTime createdAt = assertNotNull(row.get("created_at", LocalDateTime.class), "create_at");
                    InterviewSessionStatus status = InterviewSessionStatus.valueOf(assertNotNull(row.get("status", String.class), "status"));

                    InterviewMessage message = createInterviewMessageUsingReflection(id, sender, content, createdAt);

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
                                                       InterviewSender sender,
                                                       String content,
                                                       LocalDateTime createdAt){
        try {

            InterviewMessage message = new InterviewMessage();

            Field idField = InterviewMessage.class.getDeclaredField("id");
            Field senderField = InterviewMessage.class.getDeclaredField("sender");
            Field contentField = InterviewMessage.class.getDeclaredField("message"); // 필드명이 "message"라고 가정
            Field createdAtField = InterviewMessage.class.getDeclaredField("createdAt");

            idField.setAccessible(true);
            senderField.setAccessible(true);
            contentField.setAccessible(true);
            createdAtField.setAccessible(true);

            idField.set(message, id);
            senderField.set(message, sender);
            contentField.set(message, content);
            createdAtField.set(message, createdAt);

            return message;
        } catch (NoSuchFieldException|IllegalAccessException e) {
            throw new RuntimeException("Failed to set ID via reflection", e);
        }
    }
}
