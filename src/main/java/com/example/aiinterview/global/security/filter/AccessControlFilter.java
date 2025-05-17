package com.example.aiinterview.global.security.filter;

import com.example.aiinterview.global.common.utils.JWTUtils;
import com.example.aiinterview.global.security.AuthorizationPayload;
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

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessControlFilter implements WebFilter {
    private static final List<WhiteListEntry> WHITE_LIST = List.of(
            new WhiteListEntry("/api/v1/auth/login", "POST"),
            new WhiteListEntry("/api/v1/auth/refresh", "POST"),
            new WhiteListEntry("/api/v1/users", "POST"),
            new WhiteListEntry("/api/v1/users", "GET"),
            new WhiteListEntry("/swagger-ui.html", "GET"),
            new WhiteListEntry("/swagger-ui/**", "GET"),
            new WhiteListEntry("/v3/api-docs", "GET"),
            new WhiteListEntry("/v3/api-docs/**", "GET"),
            new WhiteListEntry("/webjars/**", "GET"),
            new WhiteListEntry("/actuator/**", "GET"),
            // 스트림 엔드포인트는 화이트 리스트에서 제외하고 인증 처리
            // new WhiteListEntry("/api/v1/interviews/*/messages/stream", "GET")
    );
    private final JWTUtils jwtUtils;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @NotNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().toString();

        if (isWhiteListed(path, method) || "OPTIONS".equals(method)) {
            return chain.filter(exchange);
        }

        String token = resolveToken(request, path);

        if (token == null) {
            return unauthorized(exchange);
        }

        return verifyToken(exchange, chain, token);
    }

    private boolean isWhiteListed(String path, String method) {
        return WHITE_LIST.stream()
                .anyMatch(entry -> entry.method.equalsIgnoreCase(method)
                        && pathMatcher.match(entry.pathPattern, path));
    }

    private String resolveToken(ServerHttpRequest request, String path) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        } else if (path.contains("stream")) {
            List<String> tokenParams = request.getQueryParams().get("token");
            if (tokenParams != null && !tokenParams.isEmpty()) {
                return tokenParams.get(0);
            }
        }
        return null;
    }

    private Mono<Void> verifyToken(ServerWebExchange exchange, WebFilterChain chain, String token) {
        try {
            AuthorizationPayload payload = jwtUtils.extractPayloadFromToken(token, AuthorizationPayload.class);
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

    private record WhiteListEntry(String pathPattern, String method) {
    }
}