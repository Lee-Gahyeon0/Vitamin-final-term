package com.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CSRF 비활성화 (폼 테스트 편하게)
                .csrf(csrf -> csrf.disable())

                // ★★ 모든 요청 허용 (인증 검사 하지 마) ★★
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // 기본 로그인 페이지(/login) 사용하지 않기
                .formLogin(form -> form.disable())

                // HTTP Basic 인증도 끄기
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
