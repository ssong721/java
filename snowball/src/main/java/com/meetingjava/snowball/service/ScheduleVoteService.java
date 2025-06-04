package com.meetingjava.snowball.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.entity.VoteSubmission;
import com.meetingjava.snowball.repository.ScheduleVoteRepository;
import com.meetingjava.snowball.repository.VoteSubmissionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ScheduleVoteService {

    private final ScheduleVoteRepository voteRepository;
    private final VoteSubmissionRepository voteSubmissionRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ScheduleVote findByMeetingId(String meetingId) {
        return voteRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new NoSuchElementException("해당 meetingId에 대한 투표 없음: " + meetingId));
    }

    public ScheduleVote createVote(Date start, Date end, int durationMinutes, String meetingId) {
        ScheduleVote vote = new ScheduleVote(start, end, durationMinutes, meetingId);
        return voteRepository.save(vote);
    }

    public void startVoting(String voteId) {
        ScheduleVote vote = getVoteOrThrow(voteId);
        vote.startVoting();
        voteRepository.save(vote);
    }

    // ✅ 투표 제출 - null 시간 방지 적용
    public void submitVote(String voteId, String user, List<String> selectedTimeStrs) {
    List<Date> selectedDates = convertToDateList(selectedTimeStrs);

    if (selectedDates.isEmpty()) {
        throw new IllegalArgumentException("❌ 유효한 시간 없음. 모든 시간 파싱 실패");
    }

    for (Date time : selectedDates) {
        if (time == null) continue; // 혹시라도 null이 섞였을 경우 방어
        VoteSubmission submission = new VoteSubmission(voteId, user, time);
        voteSubmissionRepository.save(submission);
    }

    ScheduleVote vote = getVoteOrThrow(voteId);
    vote.submitVote(user, selectedDates);
    voteRepository.save(vote);
}



    // ✅ 시간 변환 (밀리초 포함/미포함 모두 처리)
    private List<Date> convertToDateList(List<String> timeStrs) {
        List<Date> dates = new ArrayList<>();
        for (String timeStr : timeStrs) {
            try {
                Instant instant = Instant.parse(timeStr);
                dates.add(Date.from(instant));
            } catch (Exception e) {
                System.out.println("❌ 시간 변환 실패: " + timeStr);
            }
        }
        return dates;
    }



    public void closeVoting(String voteId) {
        ScheduleVote vote = getVoteOrThrow(voteId);
        vote.closeVoting();
        voteRepository.save(vote);
    }

    public String getVoteSummaryForGemini(String voteId) {
        List<VoteSubmission> submissions = voteSubmissionRepository.findByVoteId(voteId);
        Map<String, List<Date>> voteMap = new HashMap<>();

        for (VoteSubmission submission : submissions) {
            voteMap.computeIfAbsent(submission.getUserName(), k -> new ArrayList<>())
                   .add(submission.getSelectedTime());
        }

        ScheduleVote vote = getVoteOrThrow(voteId);
        vote.setVotes(voteMap);

        System.out.println("✅ Gemini voteMap: " + voteMap);
        System.out.println("✅ Gemini JSON: " + vote.prepareVoteDataForGPT());

        return vote.prepareVoteDataForGPT();
    }

    public void confirmTime(String voteId, Date time) {
        ScheduleVote vote = getVoteOrThrow(voteId);
        vote.confirmTime(time);
        voteRepository.save(vote);
    }

    public ScheduleVote findById(String voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new NoSuchElementException("해당 voteId 없음: " + voteId));
    }

    public List<ScheduleVote> findAll() {
        return voteRepository.findAll();
    }

    private ScheduleVote getVoteOrThrow(String voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new NoSuchElementException("해당 voteId 없음: " + voteId));
    }

    public ScheduleVote getVote(String voteId) {
        return getVoteOrThrow(voteId);
    }
}
