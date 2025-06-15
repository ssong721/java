package com.meetingjava.snowball.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.meetingjava.snowball.entity.ScheduleCandidate;
import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.entity.VoteSubmission;
import com.meetingjava.snowball.repository.ScheduleCandidateRepository;
import com.meetingjava.snowball.repository.ScheduleVoteRepository;
import com.meetingjava.snowball.repository.VoteSubmissionRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    private final ScheduleCandidateRepository scheduleCandidateRepository;

    @PostConstruct
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public ScheduleVote findByMeetingId(String meetingIdRaw) {
        String meetingId = meetingIdRaw.replaceAll("\"", "");
        return voteRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new NoSuchElementException("해당 meetingId에 대한 투표 없음: " + meetingId));
    }

    public List<ScheduleCandidate> getCandidates(String voteId) {
        ScheduleVote vote = voteRepository.findById(voteId).orElseThrow();
        String meetingId = vote.getMeetingId();

        System.out.println("✅ voteId로 조회된 meetingId: " + meetingId);

        List<ScheduleCandidate> result = scheduleCandidateRepository.findByMeetingId(meetingId);
        System.out.println("✅ 후보 일정 개수: " + result.size());

        return result;
    }

    public ScheduleVote createVote(Date start, Date end, int durationMinutes, String meetingId) {
        Optional<ScheduleVote> existing = voteRepository.findByMeetingId(meetingId);
        if (existing.isPresent()) return existing.get(); // ✅ 이미 있으면 그걸 반환

        ScheduleVote vote = new ScheduleVote(start, end, durationMinutes, meetingId);
        return voteRepository.save(vote);
    }

    public void startVoting(String voteId) {
        ScheduleVote vote = getVoteOrThrow(voteId);
        vote.startVoting();
        voteRepository.save(vote);
    }

    public void submitVote(String voteId, String user, List<String> selectedTimeStrs) {
        System.out.println("✅ 받은 voteId: " + voteId);
        
        voteId = voteId.replaceAll("\"", ""); // <-- 이 줄 추가!!!
        System.out.println("✅ 쌍따옴표 제거된 voteId: " + voteId);
        System.out.println("✅ 받은 user: " + user);
        System.out.println("✅ 받은 시간 문자열들: " + selectedTimeStrs);

        List<Date> selectedDates = convertToDateList(selectedTimeStrs);
        System.out.println("✅ 변환된 Date 리스트: " + selectedDates);

        if (selectedDates.isEmpty()) {
            System.out.println("❌ 모든 시간 파싱 실패. 예외 던짐!");
            throw new IllegalArgumentException("❌ 유효한 시간 없음. 모든 시간 파싱 실패");
        }

        ScheduleVote vote = getVoteOrThrow(voteId);
        System.out.println("✅ ScheduleVote 조회 성공: " + vote.getVoteId());

        for (Date time : selectedDates) {
            if (time == null) {
            System.out.println("⚠️ time == null 스킵");
            continue;
            }
            System.out.println("📝 저장할 VoteSubmission: " + time);
            VoteSubmission submission = new VoteSubmission(vote, user, time);
            voteSubmissionRepository.save(submission);
        }

        vote.submitVote(user, selectedDates);
        voteRepository.save(vote);

        System.out.println("✅ 투표 완료 및 저장 성공!");

    }

    private List<Date> convertToDateList(List<String> timeStrs) {
        List<Date> dates = new ArrayList<>();
        for (String timeStr : timeStrs) {
            try {
                // 기본 ISO 포맷 (Z 포함) 처리
                Instant instant = Instant.parse(timeStr);
                Date parsed = Date.from(instant);
                System.out.println("✅ Instant 파싱 성공: " + parsed);
                dates.add(parsed);
            } catch (Exception e1) {
                try {
                    // fallback: Z 포함된 포맷 명시 (주의: X는 타임존)
                    SimpleDateFormat fallbackFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                    fallbackFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date parsed = fallbackFormat.parse(timeStr);
                    System.out.println("✅ fallback 파싱 성공: " + parsed);
                    dates.add(parsed);
                } catch (Exception e2) {
                    System.out.println("❌ 시간 변환 실패: " + timeStr);
                }
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
        List<VoteSubmission> submissions = voteSubmissionRepository.findByVote_VoteId(voteId);
        Map<String, List<Date>> voteMap = new HashMap<>();

        for (VoteSubmission submission : submissions) {
            voteMap.computeIfAbsent(submission.getUserName(), k -> new ArrayList<>())
                    .add(submission.getSelectedTime());
        }

        ScheduleVote vote = getVoteOrThrow(voteId);
        vote.setVotes(voteMap);

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

    public void updateRecommendedTime(String voteId, String recommendedTimeStr) {
        try {
            Instant instant = Instant.parse(recommendedTimeStr);
            Date parsedTime = Date.from(instant);

            ScheduleVote vote = getVoteOrThrow(voteId);
            vote.setRecommendedTime(parsedTime);
            voteRepository.save(vote);

            System.out.println("✅ 추천 시간 저장 완료: " + parsedTime);
        } catch (Exception e) {
            System.out.println("❌ 추천 시간 파싱 실패: " + recommendedTimeStr);
        }
    }

    public void recommendBestTime(String meetingId) {
        ScheduleVote vote = findByMeetingId(meetingId);
        String voteId = vote.getVoteId();

        List<VoteSubmission> submissions = voteSubmissionRepository.findByVote_VoteId(voteId);
        Map<Date, Integer> countMap = new HashMap<>();

        for (VoteSubmission submission : submissions) {
            Date time = submission.getSelectedTime();
            countMap.put(time, countMap.getOrDefault(time, 0) + 1);
        }

        Date bestTime = countMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (bestTime != null) {
            vote.setRecommendedTime(bestTime);

            List<String> availableUsers = submissions.stream()
                    .filter(s -> s.getSelectedTime().equals(bestTime))
                    .map(VoteSubmission::getUserName)
                    .distinct()
                    .toList();

            vote.setAvailableUsers(availableUsers);
            voteRepository.save(vote);
        }
    }

    public Map<String, Object> getRecommendedTimeInfo(String meetingId) {
        ScheduleVote vote = findByMeetingId(meetingId);
        String voteId = vote.getVoteId();

        System.out.println("✅ voteId: " + voteId);

        List<VoteSubmission> submissions = voteSubmissionRepository.findByVote_VoteId(voteId);
        Map<Date, Integer> countMap = new HashMap<>();
        System.out.println("✅ submissions 개수: " + submissions.size());

        
        for (VoteSubmission submission : submissions) {
            Date time = submission.getSelectedTime();
            countMap.put(time, countMap.getOrDefault(time, 0) + 1);
        }

        Date bestTime = countMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        List<String> availableUsers = submissions.stream()
                .filter(s -> s.getSelectedTime().equals(bestTime))
                .map(VoteSubmission::getUserName)
                .distinct()
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("recommendedTime", bestTime);
        result.put("availableUsers", availableUsers);
        return result;
    }
}
