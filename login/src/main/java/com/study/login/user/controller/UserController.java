package com.study.login.user.controller;

import com.study.login.global.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    /**
     * 현재 인증된 사용자 emial 가져오는 메서드
     */
    @GetMapping("/users/me")
    public ResponseEntity<String> getProfile(){
        return ResponseEntity.status(HttpStatus.OK).body(SecurityUtils.getCurrentUsername());
    }
}
