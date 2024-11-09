package com.study.login.user.controller;

import com.study.login.global.utils.SecurityUtils;
import com.study.login.user.dto.*;
import com.study.login.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * 일반 회원가입 요청 처리 메서드
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignUpRequestDto requestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(requestDto));
    }

    /**
     * 일반 로그인 요청 처리 메서드
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto){
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(requestDto));
    }

    /**
     * 액세스토큰 재발급 처리 메서드
     */
    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> reissuedAccessToken(@RequestBody TokenRequestDto requestDto){
        return ResponseEntity.status(HttpStatus.OK).body(authService.reissueAccessToken(requestDto.getRefreshToken()));
    }

    /**
     * OAuth2 로그인시 추가정보(나이, 도시) 저장 메서드
     */
    @PatchMapping("/update-info")
    public ResponseEntity<String> updateInfo(@RequestBody UpdateInfoRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.updateInfo(requestDto));
    }

    /**
     * 현재 인증된 사용자 emial 가져오는 메서드
     */
    @GetMapping("/users/me")
    public ResponseEntity<String> getProfile(){
        return ResponseEntity.status(HttpStatus.OK).body(SecurityUtils.getCurrentUsername());
    }
}
