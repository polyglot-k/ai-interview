package com.example.aiinterview.module.interview.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageBufferRegistry {
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    public Mono<String> retrieveMessageBuffer(Long sessionId){
        String redisKey = generateMessageBufferKey(sessionId);
        return redisTemplate.opsForValue().get(redisKey);
    }
    //TODO: 로깅의 순서가 지켜지지 않을 수 도 있음.
    public Mono<Void> appendMessageBuffer(Long sessionId, String partialBuffer){
        String redisKey = generateMessageBufferKey(sessionId);
        return redisTemplate.opsForValue().append(redisKey, partialBuffer)
                .doOnSuccess(length -> log.debug("Room {}: Appended '{}', current length: {}", sessionId, partialBuffer, length))
                .doOnError(error -> log.error("Room {}: Error appending '{}': {}", sessionId, partialBuffer, error.getMessage()))
                .then();
    }
    public Mono<Void> clearMessageBuffer(Long sessionId){
        String redisKey = generateMessageBufferKey(sessionId);
        return redisTemplate.opsForValue().delete(redisKey)
                .then();
    }
    private String generateMessageBufferKey(Long sessionId){
        return String.format("interview:%d:partial-buffer", sessionId);
    }

}
