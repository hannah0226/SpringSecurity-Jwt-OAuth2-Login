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
}
