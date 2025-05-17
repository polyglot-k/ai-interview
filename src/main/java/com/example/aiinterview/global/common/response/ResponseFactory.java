package com.example.aiinterview.global.common.response;

import com.example.aiinterview.global.common.response.dto.ApiResponse;
import com.example.aiinterview.global.exception.BusinessException;
import com.example.aiinterview.global.exception.ErrorCode;
import com.example.aiinterview.global.common.response.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseFactory {

    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data, HttpStatus status) {
        return ResponseEntity.status(status).body(ApiResponse.success(data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(ErrorResponse.of(errorCode)));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(ErrorResponse.of(errorCode, ex.getMessage())));
    }
}

