package com.meetingjava.snowball.entity;

import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.entity.Meeting;
import com.meetingjava.snowball.repository.MeetingRepository;
import com.meetingjava.snowball.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Calendar API 연동 엔티티 클래스
 */
@Component
@RequiredArgsConstructor
public class Calendar {

    private final RestTemplate restTemplate;

    @Value("${app.api.base-url}")
    private String apiBaseUrl;

    private final ScheduleRepository scheduleRepository;
    private final MeetingRepository meetingRepository;

    /**
     * 해당 연·월의 모든 '확정 일정(Schedule)' 조회
     */
    public List<Schedule> getScheduleForMonth(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return scheduleRepository.findByStartDateBetween(start, end);
    }

    /**
     * 오늘 날짜에 해당하는 미팅만 조회
     */
    public List<Meeting> getTodayMeeting() {
        LocalDate today = LocalDate.now();
        return meetingRepository.findAll().stream()
                .filter(m -> m.getMeetingStartDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate().isEqual(today))
                .collect(Collectors.toList());
    }

    /**
     * 새 스케줄 등록
     */
    public void registerSchedule(Schedule schedule) {
        restTemplate.postForObject(
                apiBaseUrl + "/events", schedule, Schedule.class);
    }

    /**
     * 미팅 일시 변경
     */
    public void rescheduleMeeting(String meetingId, LocalDateTime newDateTime) {
        String url = String.format(
                "%s/meetings/%s?dateTime=%s",
                apiBaseUrl,
                meetingId,
                newDateTime.format(DateTimeFormatter.ISO_DATE_TIME));
        restTemplate.put(url, null);
    }
}
