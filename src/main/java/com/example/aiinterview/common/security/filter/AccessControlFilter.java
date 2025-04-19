package com.example.aiinterview.common.security.filter;

import com.example.aiinterview.common.utils.JWTUtils;
import com.example.aiinterview.common.security.AuthorizationPayload;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessControlFilter implements WebFilter {
    private static final Set<UrlMethod> WHITE_LIST_URLS = new HashSet<>();
    private final JWTUtils jwtUtils;

    static {
        WHITE_LIST_URLS.add(new UrlMethod("/api/auth/login", "POST"));
        WHITE_LIST_URLS.add(new UrlMethod("/members", "GET"));
    }

    @NotNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().toString();

        if (WHITE_LIST_URLS.contains(new UrlMethod(path, method))) {
            return chain.filter(exchange);
        }

        String authorizationHeader = request.getHeaders().getFirst("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        try {
            String token = authorizationHeader.substring(7);
            AuthorizationPayload payload = jwtUtils.extractPayloadFromToken(token, AuthorizationPayload.class);

            // ID를 attribute로 저장
            exchange.getAttributes().put("userId", payload.id());

            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("JWT 검증 실패", e);
            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    public static class UrlMethod {
        private final String url;
        private final String method;

        public UrlMethod(String url, String method) {
            this.url = url;
            this.method = method;
        }
    }
}
