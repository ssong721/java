package com.meetingjava.snowball.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String meetingId;  // 공지사항이 속한 모임 아이디

    private String title;

    private String content;

    private LocalDateTime createdAt;

    // 생성자, 게터/세터 생략 가능 (롬복 사용 시)
    public Notice() {}

    public Notice(String meetingId, String title, String content) {
        this.meetingId = meetingId;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    // 필요시 다른 Getter/Setter들도 추가
    public Long getId() {
        return id;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // setter ...
}
