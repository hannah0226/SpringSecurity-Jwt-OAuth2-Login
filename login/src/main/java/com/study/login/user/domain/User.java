package com.study.login.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity // 엔티티로 지정
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    // 기본 정보
    private String email;
    private String password;
    private String nickname;

    // 추가 정보
    private int age;
    private String city;

    // GUEST, USER
    @Enumerated(EnumType.STRING)
    private Role role;

    // GOOGLE, KAKAO
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

    // OAuth2 로그인시 Resource Server에서 가져올 수 없는 추가정보 저장
    public void updateAdditionalInfo(int age, String city){
        this.age = age;
        this.city = city;
        this.role = Role.USER;
    }
}
