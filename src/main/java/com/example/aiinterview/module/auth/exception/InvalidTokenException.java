package com.example.aiinterview.module.auth.exception;

import com.example.aiinterview.global.exception.BusinessException;
import com.example.aiinterview.global.exception.ErrorCode;

public class InvalidTokenException extends BusinessException {
    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
