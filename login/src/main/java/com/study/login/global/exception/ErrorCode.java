package com.study.login.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    ALEADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "USER-001","이미 가입된 이메일입니다."),
    ALEADY_EXIST_NICKNAME(HttpStatus.BAD_REQUEST, "USER-002","이미 존재하는 닉네임입니다."),
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER-003","해당 이메일의 유저가 존재하지 않습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "USER-004","비밀번호가 일치하지 않습니다."),

    // JWT 관련 에러 코드 추가
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "JWT-001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT-002", "만료된 토큰입니다.");
    ;

    private final HttpStatus httpStatus; //HttpStatus
    private final String code; // ex) ACCOUNT-001
    private final String message; // 설명
}
