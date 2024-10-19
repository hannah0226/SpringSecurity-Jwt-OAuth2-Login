package com.study.login.user.dto;

import com.study.login.user.domain.Role;
import com.study.login.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpRequestDto {
    private String email;
    private String password;
    private String nickname;
    private int age;
    private String city;

    public User toEntity(){
        return User.builder()
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .age(this.age)
                .city(this.city)
                .role(Role.USER)
                .build();
    }
}
