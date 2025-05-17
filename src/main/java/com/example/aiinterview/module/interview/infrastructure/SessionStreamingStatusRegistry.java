package com.example.aiinterview.module.interview.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SessionStreamingStatusRegistry {
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "session-room:streaming";

    public Mono<Boolean> isStreamingInProgress(Long sessionId) {
        return redisTemplate.opsForValue()
                .get(PREFIX + sessionId)
                .map(StreamingStatus::isInProgress)
                .defaultIfEmpty(false);
    }
    public Mono<Void> setStreamStatus(Long sessionId, StreamingStatus status) {
        return switch (status){
            case IN_PROGRESS -> startStreaming(sessionId);
            case TERMINATED -> stopStreaming(sessionId);
        };
    }
    private Mono<Void> startStreaming(Long sessionId){
        return redisTemplate.opsForValue()
                .set(PREFIX + sessionId, StreamingStatus.IN_PROGRESS.toString())
                .then();
    }

    private Mono<Void> stopStreaming(Long sessionId){
        return redisTemplate
                .delete(PREFIX + sessionId)
                .then();
    }
    public enum StreamingStatus {
        IN_PROGRESS,
        TERMINATED;

        public static Boolean isInProgress(String value) {
            if (value == null) return false;

            return StreamingStatus.valueOf(value.toUpperCase()) == IN_PROGRESS;
        }
    }
}
