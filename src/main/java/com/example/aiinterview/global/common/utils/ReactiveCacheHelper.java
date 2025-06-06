package com.example.aiinterview.global.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReactiveCacheHelper {

    private final ReactiveRedisConnectionFactory connectionFactory;
    private final Map<Class<?>, ReactiveRedisTemplate<String, ?>> templateCache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    private <T> ReactiveRedisTemplate<String, T> getRedisTemplate(Class<T> type) {
        return (ReactiveRedisTemplate<String, T>) templateCache.computeIfAbsent(type, cls -> {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(type);
            serializer.setObjectMapper(objectMapper);

            RedisSerializationContext.RedisSerializationContextBuilder<String, T> builder =
                    RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

            RedisSerializationContext<String, T> context = builder.value(serializer).build();

            return new ReactiveRedisTemplate<>(connectionFactory, context);
        });
    }
    private <T> ReactiveRedisTemplate<String, T> getRedisTemplate(TypeReference<T> typeReference) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(objectMapper.getTypeFactory().constructType(typeReference));
        serializer.setObjectMapper(objectMapper);

        RedisSerializationContext.RedisSerializationContextBuilder<String, T> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, T> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
    public <T> Mono<T> getOrSet(String key, Duration ttl, Supplier<Mono<T>> sourceSupplier, Class<T> type) {
        ReactiveRedisTemplate<String, T> redisTemplate = getRedisTemplate(type);
        return redisTemplate.opsForValue().get(key)
                .switchIfEmpty(sourceSupplier.get()
                        .flatMap(data -> redisTemplate.opsForValue().set(key, data, ttl).thenReturn(data))
                );
    }

    public <T> Mono<T> getOrSet(String key, Supplier<Mono<T>> sourceSupplier, Class<T> type) {
        ReactiveRedisTemplate<String, T> redisTemplate = getRedisTemplate(type);
        return redisTemplate.opsForValue().get(key)
                .switchIfEmpty(sourceSupplier.get()
                        .flatMap(data -> redisTemplate.opsForValue().set(key, data, Duration.ofMinutes(10)).thenReturn(data))
                );
    }

    public <T> Mono<List<T>> getOrSetList(String key, Supplier<Mono<List<T>>> sourceSupplier, Class<T> elementType) {
        TypeReference<List<T>> typeReference = new TypeReference<>() {};
        ReactiveRedisTemplate<String, List<T>> redisTemplate = getRedisTemplate(typeReference);

        return redisTemplate.opsForValue().get(key)
                .switchIfEmpty(
                        sourceSupplier.get()
                                .flatMap(list -> {
                                    if (list != null && !list.isEmpty()) {
                                        return redisTemplate.opsForValue().set(key, list, Duration.ofMinutes(10))
                                                .thenReturn(list);
                                    } else {
                                        log.error("LOG: List is empty or null for key " + key + ". Not caching.");
                                        return Mono.empty(); // 여기서 Mono.empty()를 반환하여 최종적으로는 해당 데이터 없음
                                    }
                                })
                );
    }
    public Mono<Long> invalidate(String key) {
        ReactiveRedisTemplate<String, Object> redisTemplate = getRedisTemplate(Object.class);
        return redisTemplate.delete(key);
    }

}
