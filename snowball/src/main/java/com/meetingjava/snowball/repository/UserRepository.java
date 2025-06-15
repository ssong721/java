package com.meetingjava.snowball.repository;

import org.springframework.data.jpa.repository.EntityGraph;

// 이게 데이터 베이스 연산

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import com.meetingjava.snowball.entity.*;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    @EntityGraph(attributePaths = "hostedMeetings")
    Optional<User> findWithMeetingsByUsername(String username);
    boolean existsByUsername(String username);
}
