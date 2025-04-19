package com.example.aiinterview.common.utils;

import com.example.aiinterview.common.property.JWTProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTUtils {

    private final JWTProperties jwtProperties;

    private final ObjectMapper objectMapper;

    public <T> String generateToken(T payload, long expirationToken) {
        String jsonPayload = convertPayloadToJson(payload);

        return Jwts.builder()
                .claim("payload", jsonPayload)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationToken))
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }

    private <T> String convertPayloadToJson(T payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("Payload 변환 실패", e);
            throw new RuntimeException("Payload 직렬화 실패", e);
        }
    }

    public <T> T extractPayloadFromToken(String token, Class<T> clazz) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody();

        String jsonPayload = claims.get("payload", String.class);
        log.info("Extracted JSON Payload: {}", jsonPayload);  // Payload 추출 로그 추가

        return convertJsonToPayload(jsonPayload, clazz);
    }
    private <T> T convertJsonToPayload(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Payload 역직렬화 실패", e);
            throw new RuntimeException("Payload 역직렬화 실패", e);
        }
    }
}
