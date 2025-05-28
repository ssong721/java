package com.meetingjava.snowball;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .authorizeHttpRequests()
                .requestMatchers(
                    "/",                // 루트 경로 허용
                    "/signup.html",      // index.html 직접 허용
                   "/css/**", "/js/**", "/images/**", // 정적 리소스 허용
                    "/api/auth/**"      // 로그인, 회원가입 API 허용
                ).permitAll()
                .anyRequest().authenticated();

        return http.build();
    }
}