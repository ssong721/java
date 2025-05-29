package com.meetingjava.snowball.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.meetingjava.snowball.entity.Meeting;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, String> {

    @Query("SELECT m FROM Meeting m WHERE :username MEMBER OF m.members")
    List<Meeting> findByMemberUsername(@Param("username") String username);
    

}