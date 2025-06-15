package com.meetingjava.snowball.service;

import com.meetingjava.snowball.repository.MeetingRepository;
import com.meetingjava.snowball.repository.ScheduleRepository;
import com.meetingjava.snowball.repository.ScheduleVoteRepository;
import com.meetingjava.snowball.repository.UserRepository;
import com.meetingjava.snowball.entity.Meeting;
import com.meetingjava.snowball.entity.Schedule;
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
import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final @Lazy PasswordEncoder passwordEncoder;
    private final MeetingRepository meetingRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleVoteRepository scheduleVoteRepository;

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

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }

    @Transactional
    public void deleteUserById(Long userId) {
        // 1. 유저 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다: " + userId));

        // 2. 유저가 host인 meeting 모두 조회
        List<Meeting> hostMeetings = meetingRepository.findByHostUser(user);

        for (Meeting meeting : hostMeetings) {
            // 3-1. Meeting ID로 연결된 Schedule 삭제
            scheduleRepository.deleteByMeetingId(meeting.getMeetingId());

            // 3-2. Meeting ID로 연결된 ScheduleVote 삭제
            scheduleVoteRepository.deleteByMeetingId(meeting.getMeetingId());

            // 3-3. Meeting 자체 삭제
            meetingRepository.delete(meeting);
        }

        // 4. 유저 삭제
        userRepository.delete(user);
    }
}
