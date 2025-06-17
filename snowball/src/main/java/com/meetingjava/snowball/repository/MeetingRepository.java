package com.meetingjava.snowball.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.meetingjava.snowball.entity.Meeting;
import com.meetingjava.snowball.entity.User;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, String> {

    // 사용자 이름으로 멤버 포함된 모임 찾기
    @Query("SELECT m FROM Meeting m WHERE :username MEMBER OF m.members")
    List<Meeting> findByMemberUsername(@Param("username") String username);

    // 모임 이름 검색
    List<Meeting> findByMeetingNameContaining(String meetingName);

    // 호스트 기준 모임 찾기
    List<Meeting> findByHostUser(User user);

    // ID로 모임 조회 (기존 메서드와 충돌 없음)
    Optional<Meeting> findById(String id);
}
