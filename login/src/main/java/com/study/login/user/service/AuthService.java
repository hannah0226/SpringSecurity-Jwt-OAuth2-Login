package com.study.login.user.service;

import com.study.login.global.jwt.TokenProvider;
import com.study.login.global.utils.SecurityUtils;
import com.study.login.user.domain.User;
import com.study.login.user.dto.*;
import com.study.login.global.exception.CustomException;
import com.study.login.global.exception.ErrorCode;
import com.study.login.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, String> redisTemplate;



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
        tokenProvider.saveRefreshToken(user.getId(), refreshToken);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * AccessToken 재발급
     *
     * 클라이언트가 전달한 리프레시 토큰을 검증해 유효한 경우 새로운 액세스토큰을 발급한다.
     *
     * @param refreshToken 클라이언트에서 전달된 리프레시 토큰
     * @return 새로운 AccessToken을 포함한 TokenResponseDto 객체
     */
    public TokenResponseDto reissueAccessToken(String refreshToken){
        // 전달받은 리프레시 토큰에서 이메일을 추출하여 사용자 정보 가져오기
        String email = tokenProvider.extractEmail(refreshToken);
        User user = getUserByEmail(email);

        // Redis에서 해당 사용자 Id를 키로 하는 리프래시 토큰 가져오기
        String storedRefreshToken = redisTemplate.opsForValue().get(user.getId().toString());

        // 전달받은 리프레시 토큰과 Redis에 저장된 리프레시 토큰이 일치하는지 확인
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)){
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 일치한다면 새로운 AccessToken 생성
        String accessToken = tokenProvider.createAccessToken(user);

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .build();
    }

    /**
     * OAuth2 로그인시 추가정보(나이, 도시) 저장 메서드
     *
     * Resource Server에서 받아오지 못하는 정보들을 따로 받아와 저장한다.
     * 추가 정보를 받기전까지 사용자의 권한은 GUEST이며, 추가정보를 받으면 USER로 권한을 업그레이드 한다.
     */
    public String updateInfo(UpdateInfoRequestDto requestDto) {
        String email = SecurityUtils.getCurrentUsername();
        User user = getUserByEmail(email);

        user.updateAdditionalInfo(requestDto.getAge(), requestDto.getCity());
        userRepository.save(user);
        return "사용자 정보가 저장되었습니다.";
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
