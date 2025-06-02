package com.example.aiinterview.module.interview.infrastructure.repository;

import com.example.aiinterview.global.common.utils.Snowflake;
import com.example.aiinterview.module.interview.domain.entity.InterviewDetailFeedback;
import com.example.aiinterview.module.interview.domain.repository.ReactiveBatchInsertRepository;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterviewDetailFeedbackBatchRepositoryImpl implements ReactiveBatchInsertRepository<InterviewDetailFeedback, Long> {

    private final DatabaseClient databaseClient;
    private final Snowflake snowflake;
    @Override
    public Flux<InterviewDetailFeedback> batchInsert(List<InterviewDetailFeedback> entities) {
        if (entities == null || entities.isEmpty()) {
            return Flux.empty();
        }

        String sql = "INSERT INTO interview_detail_feedback (id, feedback_text, score, created_at, m_id) VALUES (?, ?, ?, ?, ?)";
        entities.forEach(entity -> setIdUsingReflection(entity, snowflake.nextId()));
        return databaseClient.inConnectionMany(connection -> {
            Statement statement = connection.createStatement(sql);
            log.info("entities size : {}", entities.size());
            int entitySize = entities.size();
            for (int i=0;i<entitySize;i++) {
                InterviewDetailFeedback feedback = entities.get(i);
                bindStatement(statement, feedback);
                if(i < entitySize-1){
                    statement.add();
                }
            }

            return Flux.from(statement.execute()).thenMany(Flux.fromIterable(entities));
        });
    }
    private void setIdUsingReflection(InterviewDetailFeedback entity, Long id) {
        try {
            Field idField = InterviewDetailFeedback.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set ID via reflection", e);
        }
    }
    private void bindStatement(Statement statement, InterviewDetailFeedback entity) {
        bindNullable(statement, 0, entity.getId(), Long.class);
        bindNullable(statement, 1, entity.getFeedback(), String.class);
        bindNullable(statement, 2, entity.getScore(), Double.class);
        bindNullable(statement, 3, entity.getCreatedAt(), LocalDateTime.class);
        bindNullable(statement, 4, entity.getMessageId(), Long.class);
    }

    private <T> void bindNullable(Statement statement, int index, T value, Class<T> type) {
        if (value != null) {
            statement.bind(index, value);
        } else {
            statement.bindNull(index, type);
        }
    }
}