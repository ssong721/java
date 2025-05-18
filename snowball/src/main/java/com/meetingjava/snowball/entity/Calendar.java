package com.meetingjava.snowball.entity;

import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.entity.Meeting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Calendar API 연동 엔티티 클래스
 */
@Component
public class Calendar {
    private final RestTemplate restTemplate;

    @Value("${app.api.base-url}")
    private String apiBaseUrl;

    public Calendar() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 해당 연·월의 모든 스케줄 조회
     */
    public List<Schedule> getScheduleForMonth(int year, int month) {
        Schedule[] all = restTemplate.getForObject(
            apiBaseUrl + "/events", Schedule[].class);
        return Arrays.stream(all)
            .filter(s -> {
                LocalDate d = LocalDate.parse(
                    s.getStart(), DateTimeFormatter.ISO_DATE_TIME);
                return d.getYear() == year && d.getMonthValue() == month;
            })
            .collect(Collectors.toList());
    }

    /**
     * 오늘 날짜에 해당하는 미팅만 조회
     */
    public List<Meeting> getTodayMeeting() {
        Meeting[] meetings = restTemplate.getForObject(
            apiBaseUrl + "/meetings", Meeting[].class);
        LocalDate today = LocalDate.now();
        return Arrays.stream(meetings)
            .filter(m -> {
                LocalDate d = m.getMeetingStartDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
                return d.isEqual(today);
            })
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
            newDateTime.format(DateTimeFormatter.ISO_DATE_TIME)
        );
        restTemplate.put(url, null);
    }
}
