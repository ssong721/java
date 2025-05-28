package com.meetingjava.snowball.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.meetingjava.snowball.entity.Meeting;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}