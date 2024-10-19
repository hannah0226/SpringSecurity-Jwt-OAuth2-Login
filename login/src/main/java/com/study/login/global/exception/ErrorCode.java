package com.study.login.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    ALEADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "USER-001","이미 가입된 이메일입니다."),
    ALEADy_EXIST_NICKNAME(HttpStatus.BAD_REQUEST, "USER-002","이미 존재하는 닉네임입니다.")

    ;

    private final HttpStatus httpStatus; //HttpStatus
    private final String code; // ex) ACCOUNT-001
    private final String message; // 설명
}
