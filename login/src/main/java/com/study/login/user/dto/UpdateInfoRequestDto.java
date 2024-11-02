package com.study.login.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInfoRequestDto {

    @Positive(message = "Age must be a positive number")
    private int age;

    @NotEmpty(message = "City cannot be empty")
    private String city;
}
