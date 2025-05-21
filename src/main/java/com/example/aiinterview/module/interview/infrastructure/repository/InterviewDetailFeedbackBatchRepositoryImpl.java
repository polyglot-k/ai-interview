package com.example.aiinterview.module.interview.infrastructure.repository;

import com.example.aiinterview.module.interview.domain.entity.InterviewDetailFeedback;
import com.example.aiinterview.module.interview.domain.repository.ReactiveBatchInsertRepository;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterviewDetailFeedbackBatchRepositoryImpl implements ReactiveBatchInsertRepository<InterviewDetailFeedback, Long> {

    private final DatabaseClient databaseClient;

    @Override
    public Flux<InterviewDetailFeedback> batchInsert(List<InterviewDetailFeedback> entities) {
        if (entities == null || entities.isEmpty()) {
            return Flux.empty();
        }

        String sql = "INSERT INTO interview_detail_feedback (feedback_text, score, created_at, llm_id, user_id) VALUES (?, ?, ?, ?, ?)";

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

            return Flux.from(statement.execute())
                    .flatMap(result -> result.map((row, metadata) -> {
                        // 각 삽입된 행에 대해 데이터베이스에서 생성된 ID를 가져옵니다.
                        // row.toString() 및 metadata.toString()은 디버깅에 유용하나,
                        // 실제 운영 코드에서는 불필요할 수 있습니다.
                        // log.info(row.toString());
                        // log.info(metadata.toString());
                        return row.get("id", Long.class); // 생성된 ID만 추출
                    }))
                    .zipWith(Flux.fromIterable(entities), (generatedId, originalEntity) -> {
                        // 원본 엔티티의 데이터를 사용하여 새로운 InterviewDetailFeedback 인스턴스를 생성하고,
                        // 여기에 새로 생성된 ID를 부여합니다.
                        // InterviewDetailFeedback.of() 메서드가 ID를 포함한 모든 필드를 인자로 받는다고 가정합니다.
                        return InterviewDetailFeedback.of(
                                generatedId, // 데이터베이스에서 생성된 ID
                                originalEntity.getFeedback(),
                                originalEntity.getScore(),
                                originalEntity.getCreatedAt(),
                                originalEntity.getLlmMessageId(),
                                originalEntity.getUserMessageId()
                        );
                    });
        });
    }

    private void bindStatement(Statement statement, InterviewDetailFeedback entity) {
        bindNullable(statement, 0, entity.getFeedback(), String.class);
        bindNullable(statement, 1, entity.getScore(), Double.class);
        bindNullable(statement, 2, entity.getCreatedAt(), LocalDateTime.class);
        bindNullable(statement, 3, entity.getLlmMessageId(), Long.class);
        bindNullable(statement, 4, entity.getUserMessageId(), Long.class);
    }

    private <T> void bindNullable(Statement statement, int index, T value, Class<T> type) {
        if (value != null) {
            statement.bind(index, value);
        } else {
            statement.bindNull(index, type);
        }
    }
}