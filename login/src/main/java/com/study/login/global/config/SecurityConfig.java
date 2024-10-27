package com.study.login.global.config;

import com.study.login.global.filter.JwtAuthenticationFilter;
import com.study.login.global.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenProvider tokenProvider;

    /**
     * CORS 설정을 위한 Bean 등록
     * 프론트엔드가 위치한 도메인과 CORS 통신을 허용하기 위해 설정함
     *
     * @return CORS 설정이 적용된 CorsConfigurationSource 객체
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000/");
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * SecurityFilterChain 설정을 위한 Bean 등록
     * HTTP 요청에 대한 보안 구성을 정의하고, JWT 인증 필터 추가
     *
     * @param http HttpSecurity 설정 객체
     * @return 설정이 완료된 SecurityFilterChain 객체
     * @throws Exception 설정 중 예외 발생 시
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 기본 인증 방식 비활성화 (UI 대신 토큰을 통한 인증을 사용하기 때문)
                .httpBasic(AbstractHttpConfigurer::disable)
                // CSRF 보호 비활성화 (토큰 기반 인증이므로 필요하지 않음)
                .csrf(AbstractHttpConfigurer::disable)
                // CORS 설정 적용
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                // 요청에 따른 인증 인가 설정
                .authorizeHttpRequests(requests -> {
                    /* 회원가입, 로그인, 액세스토큰 재발급, GET요청은 모두 허용 */
                    requests.requestMatchers("auth/signup","auth/login","auth/token").permitAll();
                    requests.requestMatchers(HttpMethod.GET).permitAll();
                    /* 다른 모든 요청은 인증을 요구 */
                    requests.anyRequest().authenticated();
                })
                // JWT를 사용하므로 sateless
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    /**
     * BCryptPasswordEncoder를 Bean으로 등록
     * 사용자 비밀번호 암호화를 위해 Spring Security에서 제공하는 BCryptPasswordEncoder 사용
     *
     * @return BCryptPasswordEncoder 객체
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
