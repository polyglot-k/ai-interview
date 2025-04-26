package com.example.aiinterview.global.security.filter;

import com.example.aiinterview.global.security.AuthorizationPayload;
import com.example.aiinterview.global.common.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessControlFilter implements WebFilter {
    private static final List<WhiteListEntry> WHITE_LIST = new ArrayList<>();
    private final JWTUtils jwtUtils;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    static {
        WHITE_LIST.add(new WhiteListEntry("/api/auth/login", "POST"));
        WHITE_LIST.add(new WhiteListEntry("/members", "GET"));
        WHITE_LIST.add(new WhiteListEntry("/swagger-ui.html", "GET"));
        WHITE_LIST.add(new WhiteListEntry("/swagger-ui/**", "GET"));
        WHITE_LIST.add(new WhiteListEntry("/v3/api-docs", "GET"));               // 핵심
        WHITE_LIST.add(new WhiteListEntry("/v3/api-docs/**", "GET"));            // 보통 이게 중요
        WHITE_LIST.add(new WhiteListEntry("/webjars/**", "GET"));
    }

    @NotNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().toString();

        if (isWhiteListed(path, method)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        try {
            String token = authHeader.substring(7);
            AuthorizationPayload payload = jwtUtils.extractPayloadFromToken(token, AuthorizationPayload.class);

            // JWT에서 추출한 사용자 ID를 attribute로 저장
            exchange.getAttributes().put("userId", payload.id());

            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("JWT 검증 실패", e);
            return unauthorized(exchange);
        }
    }

    private boolean isWhiteListed(String path, String method) {
        return WHITE_LIST.stream()
                .anyMatch(entry -> entry.method.equalsIgnoreCase(method)
                        && pathMatcher.match(entry.pathPattern, path));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    private static class WhiteListEntry {
        private final String pathPattern;
        private final String method;

        public WhiteListEntry(String pathPattern, String method) {
            this.pathPattern = pathPattern;
            this.method = method;
        }
    }
}
