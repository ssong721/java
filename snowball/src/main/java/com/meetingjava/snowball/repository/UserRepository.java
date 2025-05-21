package com.meetingjava.snowball.repository;

// 이게 데이터 베이스 연산

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.meetingjava.snowball.entity.*;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
