package com.meetingjava.snowball.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;     // 출석한 사용자명
    private String meetingId;    // 해당 모임 ID
    private LocalDate attendanceDate; // 출석한 날짜
    private boolean present;     // 출석 여부 (true = 출석)

    @Column(name = "schedule_id")
    private Long scheduleId;

}
