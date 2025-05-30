package com.meetingjava.snowball.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // ❗당신이 만든 User 클래스 import

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    private LocalDate joinedAt;
    private double attendanceRate;

    @Enumerated(EnumType.STRING)
    private Role role;

    // 생성자 편의 함수
    public Member(User user, Meeting meeting, Role role) {
        this.user = user;
        this.meeting = meeting;
        this.role = role;
        this.joinedAt = LocalDate.now();
        this.attendanceRate = 0.0;
    }

    public boolean isHost() {
        return this.role == Role.HOST;
    }

}
