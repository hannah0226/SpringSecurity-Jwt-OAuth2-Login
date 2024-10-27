package com.study.login.user.dto;

import com.study.login.user.domain.Role;
import com.study.login.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpRequestDto {

    private String email;
    private String password;
    private String nickname;
    private int age;
    private String city;

    public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder){
        return User.builder()
                .email(this.email)
                .password(bCryptPasswordEncoder.encode(this.password))
                .nickname(this.nickname)
                .age(this.age)
                .city(this.city)
                .role(Role.USER)
                .build();
    }
}
