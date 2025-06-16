package com.meetingjava.snowball.service;

import com.meetingjava.snowball.entity.Check;
import com.meetingjava.snowball.repository.CheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CheckService {

    @Autowired
    private CheckRepository checkRepository;

    public Check create(String meetingId, String question, String answer) {
        // 기존에 같은 meetingId 질문 있으면 삭제
        checkRepository.deleteByMeetingId(meetingId);

        Check check = Check.builder()
                .meetingId(meetingId)
                .question(question)
                .answer(answer)
                .enable(true)
                .method("subjective")
                .build();

        return checkRepository.save(check);
    }

    public Optional<Check> getByMeetingId(String meetingId) {
        return checkRepository.findByMeetingId(meetingId);
    }

    public boolean isCorrectAnswer(String meetingId, String userAnswer) {
        Optional<Check> checkOpt = checkRepository.findByMeetingId(meetingId);
        return checkOpt.filter(c -> c.getAnswer().equals(userAnswer)).isPresent();
    }

    public void deleteByMeetingId(String meetingId) {
        checkRepository.deleteByMeetingId(meetingId);
    }
}

