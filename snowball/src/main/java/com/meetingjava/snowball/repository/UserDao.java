package com.meetingjava.snowball.repository;

// 이게 데이터 베이스 연산

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import java.sql.*;

import com.meetingjava.snowball.entity.User;

@Repository
public class UserDao {
    @Value("${spring.datasource.url}")      
    private String url;
    @Value("${spring.datasource.username}") 
    private String user;
    @Value("${spring.datasource.password}") 
    private String pass;

    private Connection conn;

    @PostConstruct
    public void init() throws SQLException {
        conn = DriverManager.getConnection(url, user, pass);
    }

    public User findByKakaoId(long kakaoId) throws SQLException {
        String sql = "SELECT id, kakao_id, nickname, email, created_at FROM users WHERE kakao_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, kakaoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setKakaoId(rs.getLong("kakao_id"));
                    u.setNickname(rs.getString("nickname"));
                    u.setEmail(rs.getString("email"));
                    u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return u;
                }
            }
        }
        return null;
    }

    public User insertKakaoUser(User u) throws SQLException {
        String sql = "INSERT INTO users (kakao_id, nickname, email) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, u.getKakaoId());
            ps.setString(2, u.getNickname());
            ps.setString(3, u.getEmail());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    u.setId(keys.getInt(1));
                }
            }
        }
        return u;
    }
}