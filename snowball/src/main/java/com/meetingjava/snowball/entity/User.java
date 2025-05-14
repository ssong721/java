package com;

// 얘가 변수보관

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {
    private int id;
    private long kakaoId;
    private String nickname;
    private String email;
    private LocalDateTime createdAt;

    public User() {}

    public User(long kakaoId, String nickname, String email) {
        this.kakaoId   = kakaoId;
        this.nickname  = nickname;
        this.email     = email;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getKakaoId() { return kakaoId; }
    public void setKakaoId(long kakaoId) { this.kakaoId = kakaoId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
