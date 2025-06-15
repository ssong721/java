package com.meetingjava.snowball.dto;

import com.meetingjava.snowball.entity.ScheduleCandidate;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ScheduleMakeDto {
    private String meetingId;
    private String scheduleName;
    private String startDate;
    private String endDate;

    // 시간 관련 값들을 나눠서 받는다
    private String startHour;
    private String startMin;
    private String startAMPM;

    private String endHour;
    private String endMin;
    private String endAMPM;

    public ScheduleCandidate toCandidateEntity() {
        try {
            System.out.println("📦 startDate: " + startDate + ", endDate: " + endDate);
            System.out.println("⏰ 시간: " + startHour + ":" + startMin + " " + startAMPM);

            // 날짜 파싱
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            // 시간 파싱
            int sHour = Integer.parseInt(startHour);
            int eHour = Integer.parseInt(endHour);

            if ("PM".equals(startAMPM) && sHour < 12)
                sHour += 12;
            if ("AM".equals(startAMPM) && sHour == 12)
                sHour = 0;
            if ("PM".equals(endAMPM) && eHour < 12)
                eHour += 12;
            if ("AM".equals(endAMPM) && eHour == 12)
                eHour = 0;

            LocalTime sTime = LocalTime.of(sHour, Integer.parseInt(startMin));
            LocalTime eTime = LocalTime.of(eHour, Integer.parseInt(endMin));

            return ScheduleCandidate.builder()
                    .scheduleName(scheduleName)
                    .startDate(start)
                    .endDate(end)
                    .startTime(sTime)
                    .endTime(eTime)
                    .meetingId(meetingId)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("toEntity() 변환 실패", e);
        }
    }
}
