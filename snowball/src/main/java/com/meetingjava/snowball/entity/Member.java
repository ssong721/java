package com.meetingjava.snowball.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import com.meetingjava.snowball.entity.Meeting;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class Member {

    private Meeting meeting;
    private User user;
    private LocalDate joinedAt;
    private double attendanceRate;
    private List<Feedback> feedbacks;
    private Role role;

    public double getAttendaceRate() {
        return this.attendanceRate;
    }
    
    @Builder
    public void submitFeedback(String content) {
        Feedback feedback = Feedback.builder()
                .member(this)
                .content(content)
                .submittedAt(LocalDate.now())
                .build();

        this.feedbacks.add(feedback);
    }

    public LocalDate getJoinedDate() {
        return this.joinedAt;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return this.role;
    }

}
