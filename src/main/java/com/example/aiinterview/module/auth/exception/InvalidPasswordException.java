package com.example.aiinterview.module.auth.exception;

import com.example.aiinterview.global.exception.BusinessException;
import com.example.aiinterview.global.exception.ErrorCode;

public class InvalidPasswordException extends BusinessException {
    public InvalidPasswordException() {
        super(ErrorCode.INVALID_PASSWORD);
    }
}
