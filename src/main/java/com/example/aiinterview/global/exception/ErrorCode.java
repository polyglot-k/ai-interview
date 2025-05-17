package com.example.aiinterview.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    INVALID_INPUT("E001", "잘못된 입력입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND("E002", "찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTERNAL_ERROR("E999", "서버 내부 오류", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCESS_DENIED("S001", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    //auth
    INVALID_TOKEN("A001", "토큰 이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD("U003", "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

    //USER
    DUPLICATE_EMAIL("U001", "이미 존재하는 이메일입니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("U002", "유저를 찾을 수 없습니다..", HttpStatus.NOT_FOUND),

    //Interview
    INTERVIEW_ALREADY_ENDED("I001", "이미 종료된 면접입니다.", HttpStatus.BAD_REQUEST),
    INTERVIEW_SESSION_NOT_FOUND("I002", "면접방이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    STREAMING_ALREADY_IN_PROGRESS("I003", "이미 스트리밍이 진행 중입니다.", HttpStatus.CONFLICT);
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;


}
