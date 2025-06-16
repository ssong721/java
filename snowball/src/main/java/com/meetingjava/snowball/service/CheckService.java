package com.meetingjava.snowball.service;

import com.meetingjava.snowball.entity.Check;
import com.meetingjava.snowball.repository.AttendanceRepository;
import com.meetingjava.snowball.repository.CheckRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CheckService {

    private final CheckRepository checkRepository;
    private final AttendanceRepository attendanceRepository;

    public CheckService(CheckRepository checkRepository, AttendanceRepository attendanceRepository) {
        this.checkRepository = checkRepository;
        this.attendanceRepository = attendanceRepository;
    }

    // 특정 모임의 체크 정보 1건 가져오기 (예외 발생 방지)
    public Optional<Check> getByMeetingId(String meetingId) {
        return checkRepository.findByMeetingId(meetingId);
    }

    // 체크 생성
    public void create(String meetingId, String question, String answer) {
        Check check = Check.builder()
                .meetingId(meetingId)
                .question(question)
                .answer(answer)
                .enable(true)
                .build();
        checkRepository.save(check);
    }

    // 정답 확인
    public boolean isCorrectAnswer(String meetingId, String userAnswer) {
        Optional<Check> optionalCheck = getByMeetingId(meetingId);
        return optionalCheck
                .map(check -> check.getAnswer().trim().equalsIgnoreCase(userAnswer.trim()))
                .orElse(false);
    }

    // 체크 + 출석 삭제
    @Transactional
    public void deleteByMeetingId(String meetingId) {
        checkRepository.deleteByMeetingId(meetingId);
        attendanceRepository.deleteByMeetingId(meetingId);
    }
}


