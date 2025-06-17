package com.meetingjava.snowball.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.meetingjava.snowball.dto.DashboardResponse;
import com.meetingjava.snowball.entity.Meeting;
import com.meetingjava.snowball.entity.Schedule;
import com.meetingjava.snowball.repository.MemberRepository;

@Service
public class DashboardService {

    private final StaticService staticService;
    private final NoticeService noticeService;
    private final ScheduleService scheduleService;
    private final MemberRepository memberRepository;
    private final MeetingService meetingService;

    public DashboardService(StaticService staticService,
                            NoticeService noticeService,
                            ScheduleService scheduleService,
                            MemberRepository memberRepository,
                            MeetingService meetingService) {
        this.staticService = staticService;
        this.noticeService = noticeService;
        this.scheduleService = scheduleService;
        this.memberRepository = memberRepository;
        this.meetingService = meetingService;
    }

    // 대시보드 전체 데이터 (API 용)
    public DashboardResponse getDashboardData(String meetingId) {
        float groupRate = staticService.calculateAttendanceRate(meetingId);
        Schedule nextMeeting = staticService.getUpcomingSchedule(meetingId).orElse(null);

        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        List<Schedule> calendarList = staticService.getAllSchedules().stream()
                .filter(s -> s.getMeetingId().equals(meetingId))
                .filter(s -> {
                    LocalDate date = s.getStartDate();
                    return !date.isBefore(start) && !date.isAfter(end);
                })
                .toList();

        String noticeTitle = "공지사항 없음";

        return new DashboardResponse(groupRate, nextMeeting, calendarList, noticeTitle);
    }

    // ✔️ 컨트롤러용 메서드들

    public double getGroupAttendanceRate(String meetingId) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) return 0.0;

        List<Schedule> schedules = scheduleService.getSchedulesByMeetingId(meetingId);
        int totalMembers = memberRepository.countByMeeting(meeting);

        return staticService.calculateAccurateAttendanceRate(meetingId, schedules, totalMembers);
    }

    public double getUserAttendanceRate(String username, String meetingId) {
        return staticService.calculateUserAttendanceRate(username, meetingId);
    }

    public String getNextMeetingInfo(String meetingId) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return staticService.getUpcomingSchedule(meetingId)
                .map(s -> String.format(
                        "%s (%s %s ~ %s)",
                        s.getScheduleName(),
                        s.getStartDate().format(dateFormatter),
                        s.getStartTime().format(timeFormatter),
                        s.getEndTime().format(timeFormatter)
                ))
                .orElse("예정된 모임이 없습니다.");
    }
}

    
    
