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

    public Mono<Boolean> isStreamingInProgress(Long roomId) {
        return redisTemplate.opsForValue()
                .get(PREFIX + roomId)
                .map(StreamingStatus::isInProgress)
                .defaultIfEmpty(false);
    }
    public Mono<Void> setStreamStatus(Long roomId, StreamingStatus status) {
        return switch (status){
            case IN_PROGRESS -> startStreaming(roomId);
            case TERMINATED -> stopStreaming(roomId);
        };
    }
    private Mono<Void> startStreaming(Long roomId){
        return redisTemplate.opsForValue()
                .set(PREFIX + roomId, StreamingStatus.IN_PROGRESS.toString())
                .then();
    }

    private Mono<Void> stopStreaming(Long roomId){
        return redisTemplate
                .delete(PREFIX + roomId)
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
