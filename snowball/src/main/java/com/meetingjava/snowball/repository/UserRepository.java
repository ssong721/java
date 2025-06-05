package com.meetingjava.snowball.repository;

// 이게 데이터 베이스 연산

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import com.meetingjava.snowball.entity.*;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.hostedMeetings WHERE u.username = :username")
    Optional<User> findWithMeetingsByUsername(@Param("username") String username);
    boolean existsByUsername(String username);
    void deleteByUsername(String username);
}
