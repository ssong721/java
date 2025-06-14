package com.meetingjava.snowball.dto;

import com.meetingjava.snowball.entity.Schedule;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ScheduleMakeDto {
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

    public Schedule toEntity() {
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

        return Schedule.builder()
                .scheduleName(scheduleName)
                .startDate(start) // ✅ 시작일
                .endDate(end) // ✅ 종료일
                .startTime(sTime)
                .endTime(eTime)
                .build();
    }
}
