package com.meetingjava.snowball.repository;

import com.meetingjava.snowball.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, String> {
    // 필요하면 커스텀 쿼리도 추가 가능하다고 하네요!
}
