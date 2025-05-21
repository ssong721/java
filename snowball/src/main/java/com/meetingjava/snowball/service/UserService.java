package com.meetingjava.snowball.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.meetingjava.snowball.repository.*;
import com.meetingjava.snowball.entity.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String username, String password, String name) {
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(null, username, encodedPassword, name);
        userRepository.save(user);
    }

    public User login(String username, String password) {
        return userRepository.findByUsername(username)
            .filter(user -> passwordEncoder.matches(password, user.getPassword()))
            .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 틀렸습니다."));
    }
}
