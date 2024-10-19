package com.study.login.user.dto;

import com.study.login.user.domain.Role;
import com.study.login.user.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
