package com.meetingjava.snowball.service;

import com.meetingjava.snowball.repository.UserRepository;
import com.meetingjava.snowball.entity.User;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.transaction.Transactional;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final @Lazy PasswordEncoder passwordEncoder;

    public void registerUser(String username, String password, String name) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setName(name);
        userRepository.save(user);
    }

    // ✅ 직접 로그인 확인하고 싶은 경우 사용할 메서드
    public User login(String username, String password) {
        return userRepository.findByUsername(username)
            .filter(user -> passwordEncoder.matches(password, user.getPassword()))
            .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 틀렸습니다."));
    }

    // 🔒 Spring Security 전용
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())  // 암호화된 비밀번호
                .roles("USER")
                .build();
    }

    private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

    public void logout(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }
        if (tokenBlacklist.contains(token)) {
            throw new IllegalStateException("이미 로그아웃된 토큰입니다.");
        }
        tokenBlacklist.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }

    @Transactional
    public void deleteByUsername(String username) {
        // 연관된 meetings도 함께 가져옴
        User user = userRepository.findWithMeetingsByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // user가 host로 등록된 meeting들은 JPA cascade로 자동 삭제되도록 구성돼 있어야 함
        userRepository.delete(user);
    }

}
