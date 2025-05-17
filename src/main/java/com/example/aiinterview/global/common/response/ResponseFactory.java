package com.example.aiinterview.global.common.response;

import com.example.aiinterview.global.common.response.dto.ApiResponse;
import com.example.aiinterview.global.common.response.dto.ErrorResponse;
import com.example.aiinterview.global.exception.BusinessException;
import com.example.aiinterview.global.exception.ErrorCode;
import reactor.core.publisher.Mono;

public class ResponseFactory {

    public static <T> Mono<ApiResponse<T>> successMono(T data) {
        return Mono.just(ApiResponse.success(data));
    }
    public static Mono<ApiResponse<Void>> successVoid() {
        return Mono.just(ApiResponse.success(null));
    }

    public static <T> Mono<ApiResponse<T>> errorMono(ErrorCode errorCode) {
        return Mono.just(ApiResponse.error(ErrorResponse.of(errorCode)));
    }

    public static <T> Mono<ApiResponse<T>> errorMono(BusinessException ex) {
        return Mono.just(ApiResponse.error(ErrorResponse.of(ex.getErrorCode(), ex.getMessage())));
    }
}