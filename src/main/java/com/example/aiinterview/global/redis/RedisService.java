package com.example.aiinterview.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    // 값 저장
    public Mono<Boolean> save(String key, String value) {
        return redisTemplate.opsForValue().set(key, value);
    }

    // 값 조회
    public Mono<String> get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Mono<Long> appendToString(String key, String value) {
        return redisTemplate.opsForValue().append(key, value);
    }

    // 삭제: 키에 해당하는 값을 Redis에서 삭제
    public Mono<Boolean> delete(String key) {
        return redisTemplate.opsForValue().delete(key);
    }
}
