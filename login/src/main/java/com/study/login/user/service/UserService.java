package com.study.login.user.service;

import com.study.login.user.domain.User;
import com.study.login.user.dto.SignUpRequestDto;
import com.study.login.global.exception.CustomException;
import com.study.login.global.exception.ErrorCode;
import com.study.login.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 자체 로그인 회원가입
    public String signup(SignUpRequestDto requestDto){
        // 이메일과 닉네임이 중복되는지 확인
        if(isEmailExist(requestDto.getEmail())){
            throw new CustomException(ErrorCode.ALEADY_EXIST_EMAIL);
        }
        if(isNicknameExist(requestDto.getNickname())){
            throw new CustomException(ErrorCode.ALEADy_EXIST_NICKNAME);
        }

        // 이메일과 닉네임이 중복되지 않는다면 유저 저장
        User user = userRepository.save(requestDto.toEntity());
        return(user.getNickname() + "님의 회원가입이 완료되었습니다.");
    }


    /* Transactional 함수들 */
    // 존재하는 이메일인지 확인하는 함수
    @Transactional(readOnly = true)
    public boolean isEmailExist(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    // 존재하는 닉네임인지 확인하는 함수
    @Transactional(readOnly = true)
    public boolean isNicknameExist(String nickname){
        return userRepository.findByNickname(nickname).isPresent();
    }
}
