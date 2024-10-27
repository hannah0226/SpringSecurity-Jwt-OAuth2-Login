package com.study.login.user.service;

import com.study.login.global.jwt.TokenProvider;
import com.study.login.user.domain.User;
import com.study.login.user.dto.LoginRequestDto;
import com.study.login.user.dto.LoginResponseDto;
import com.study.login.user.dto.SignUpRequestDto;
import com.study.login.global.exception.CustomException;
import com.study.login.global.exception.ErrorCode;
import com.study.login.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Auth 회원가입
     * 중복된 이메일이나 닉네임이 존재할 경우 예외를 발생시킴
     */
    public String signup(SignUpRequestDto requestDto){
        // 이메일과 닉네임이 중복되는지 확인
        if(isEmailExist(requestDto.getEmail())){
            throw new CustomException(ErrorCode.ALEADY_EXIST_EMAIL);
        }
        if(isNicknameExist(requestDto.getNickname())){
            throw new CustomException(ErrorCode.ALEADY_EXIST_NICKNAME);
        }

        // 이메일과 닉네임이 중복되지 않는다면 사용자 저장 후 완료 메세지 반환
        User user = userRepository.save(requestDto.toEntity(bCryptPasswordEncoder));
        return(user.getNickname() + "님의 회원가입이 완료되었습니다.");
    }

    /**
     * Auth 로그인
     *
     * @param requestDto 로그인 요청 데이터 (이메일, 비밀번호)
     * @return 로그인 응답 데이터 (액세스 토큰, 리프레시 토큰)
     */
    public LoginResponseDto login(LoginRequestDto requestDto){
        // email로 유저 객체 가져오기
        User user = getUserByEmail(requestDto.getEmail());

        // 비밀번호가 일치하는지 확인
        if (!bCryptPasswordEncoder.matches(requestDto.getPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 로그인 성공 -> 액세스토큰, 리프레시토큰 생성
        String accessToken = tokenProvider.createAccessToken(user);
        String refreshToken = tokenProvider.createRefreshToken(user);

        // 리프레시토큰 저장

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    /* Transactional 함수들 */

    // 존재하는 이메일인지 확인
    @Transactional(readOnly = true)
    public boolean isEmailExist(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    // 존재하는 닉네임인지 확인
    @Transactional(readOnly = true)
    public boolean isNicknameExist(String nickname){
        return userRepository.findByNickname(nickname).isPresent();
    }

    // Email로 사용자 객체 가져오기
    @Transactional(readOnly = true)
    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_EXIST));
    }
}
