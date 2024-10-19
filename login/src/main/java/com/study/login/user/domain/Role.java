package com.study.login.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor // final 또는 @NotNull이 붙은 필드의 생성자를 자동으로 생성
public enum Role {
    /**
     * OAuth2로그인에서 추가정보를 입력받기 위한 ROLE
     *
     * OAuth2 로그인: 첫 로그인시 GUEST로 설정, 추가 정보 입력시 USER로 업데이트
     * 자체 로그인: 회원가입시 상관없이 USER로 설정되어 DB에 저장
     */

    GUEST("ROLE_GUEST"),
    USER("ROLE_USER");

    private final String key;
}
