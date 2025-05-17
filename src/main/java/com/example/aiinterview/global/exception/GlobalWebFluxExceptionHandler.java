package com.example.aiinterview.global.exception;

import com.example.aiinterview.global.common.response.dto.ApiResponse;
import com.example.aiinterview.global.common.response.dto.ErrorResponse;
import com.example.aiinterview.global.common.utils.JsonUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GlobalWebFluxExceptionHandler implements WebExceptionHandler {

    @NotNull
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, @NotNull Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        ErrorCode errorCode;
        String message;

        if (ex instanceof BusinessException businessEx) {
            errorCode = businessEx.getErrorCode();
            message = ex.getMessage();
        } else {
            errorCode = ErrorCode.INTERNAL_ERROR;
            message = "서버 내부 오류가 발생했습니다.";
        }

        ApiResponse<Object> response = ApiResponse.error(
                ErrorResponse.of(errorCode, message)
        );

        byte[] bytes = JsonUtil.toJsonBytes(response);

        exchange.getResponse().setStatusCode(errorCode.getHttpStatus());
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(bytes)));
    }
}

