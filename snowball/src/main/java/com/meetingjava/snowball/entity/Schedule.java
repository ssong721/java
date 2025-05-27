package com.meetingjava.snowball.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String scheduleName;
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;

    // ✅ 추가: meetingId 필드
    private String meetingId;

    public Schedule() {}

    public Schedule(String scheduleName, LocalDate scheduleDate, LocalTime startTime, LocalTime endTime) {
        this.scheduleName = scheduleName;
        this.scheduleDate = scheduleDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public String getStart() {
        return scheduleDate
            .atTime(startTime)
            .format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getEnd() {
        return scheduleDate
            .atTime(endTime)
            .format(DateTimeFormatter.ISO_DATE_TIME);
    }

    // ✅ 추가: meetingId getter/setter
    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public boolean isOverlapping(Schedule other) {
        return this.scheduleDate.equals(other.scheduleDate) &&
               !(this.endTime.isBefore(other.startTime) || this.startTime.isAfter(other.endTime));
    }

    public void editSchedule(String newName, LocalDate newDate, LocalTime newStart, LocalTime newEnd) {
        this.scheduleName = newName;
        this.scheduleDate = newDate;
        this.startTime = newStart;
        this.endTime = newEnd;
    }

    public String convertToCalendarEvent() {
        return String.format("%s - %s: %s", startTime, endTime, scheduleName);
    }
}
