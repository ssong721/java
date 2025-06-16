package com.meetingjava.snowball.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 전체 필드 생성자
@Builder // 빌더 패턴
@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String scheduleName;

    private LocalDate startDate; // ✅ 일정 시작일
    private LocalDate endDate; // ✅ 일정 종료일

    private LocalTime startTime;
    private LocalTime endTime;

    // 모임 구분용 필드 (예: 어떤 모임에 속한 일정인지)
    private String meetingId;
    
    @Column(unique = true)
    private String voteId;

    // 시작일 + 시작시간 → ISO 8601 형식 문자열 반환
    public String getStart() {
        return startDate
                .atTime(startTime)
                .format(DateTimeFormatter.ISO_DATE_TIME);
    }

    // 종료일 + 종료시간 → ISO 8601 형식 문자열 반환
    public String getEnd() {
        return endDate
                .atTime(endTime)
                .format(DateTimeFormatter.ISO_DATE_TIME);
    }

    // 일정 겹침 여부 판별 (단일일 기준, 필요시 확장 가능)
    public boolean isOverlapping(Schedule other) {
        return this.startDate.equals(other.startDate) &&
                !(this.endTime.isBefore(other.startTime) || this.startTime.isAfter(other.endTime));
    }

    // 일정 수정 로직
    public void editSchedule(String newName, LocalDate newStartDate, LocalDate newEndDate, LocalTime newStart,
            LocalTime newEnd) {
        this.scheduleName = newName;
        this.startDate = newStartDate;
        this.endDate = newEndDate;
        this.startTime = newStart;
        this.endTime = newEnd;
    }

    // 캘린더 출력용 문자열
    public String convertToCalendarEvent() {
        return String.format("%s - %s: %s", startTime, endTime, scheduleName);
    }
}
