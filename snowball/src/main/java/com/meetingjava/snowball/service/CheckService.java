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

    // 특정 모임에 대한 체크 정보 조회
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
        Optional<Check> optionalCheck = checkRepository.findByMeetingId(meetingId);
        if (optionalCheck.isPresent()) {
            Check check = optionalCheck.get();
            boolean correct = check.getAnswer().trim().equalsIgnoreCase(userAnswer.trim());

            if (correct) {
                // 출석 인정 (present = true 로 저장)
                // 이미 출석 데이터가 있는지 확인하고 처리할 수도 있음
                // 이 부분은 필요에 따라 확장 가능
            }

            return correct;
        }
        return false;
    }

    // 체크 삭제 + 출석도 삭제
    @Transactional
    public void deleteByMeetingId(String meetingId) {
        checkRepository.deleteByMeetingId(meetingId);
        attendanceRepository.deleteByMeetingId(meetingId);
    }
}


