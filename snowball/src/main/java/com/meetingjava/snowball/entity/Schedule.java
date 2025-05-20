package com.meetingjava.snowball.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String scheduleName;
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;

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

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
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
