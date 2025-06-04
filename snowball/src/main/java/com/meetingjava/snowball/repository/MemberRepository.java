package com.meetingjava.snowball.repository;

import com.meetingjava.snowball.entity.Member;
import com.meetingjava.snowball.entity.User;
import com.meetingjava.snowball.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByUserAndMeeting(User user, Meeting meeting);
    List<Member> findByMeeting(Meeting meeting);
    int countByMeeting(Meeting meeting);
}
