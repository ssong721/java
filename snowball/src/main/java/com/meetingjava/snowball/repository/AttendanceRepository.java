package com.meetingjava.snowball.repository;

import com.meetingjava.snowball.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.meetingjava.snowball.repository.AttendanceRepository;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // 전체 모임 기준 출석 기록
    List<Attendance> findByMeetingId(String meetingId);

    // 사용자 & 모임 기준 출석 기록
    List<Attendance> findByUsernameAndMeetingId(String username, String meetingId);

    // 모임 & 날짜 기준 출석 기록
    List<Attendance> findByMeetingIdAndAttendanceDate(String meetingId, LocalDate date);

    // 사용자 & 모임 & 날짜 기준 출석 기록 존재 여부 (중복 방지용)
    boolean existsByUsernameAndMeetingIdAndAttendanceDate(String username, String meetingId, LocalDate attendanceDate);

    // 출석기록 전체 삭제 (모임 기준)
    @Modifying
    @Query("DELETE FROM Attendance a WHERE a.meetingId = :meetingId")
    void deleteByMeetingId(@Param("meetingId") String meetingId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.meetingId = :meetingId AND a.scheduleId = :scheduleId AND a.present = true")
    int countPresentMembers(@Param("meetingId") String meetingId, @Param("scheduleId") Long scheduleId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.meetingId = :meetingId AND a.present = true")
    int countPresentByMeetingId(@Param("meetingId") String meetingId);
    
}


