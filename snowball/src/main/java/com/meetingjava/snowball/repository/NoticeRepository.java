package com.meetingjava.snowball.repository;

import com.meetingjava.snowball.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // meetingId로 공지사항 리스트 조회, 최신순 정렬
    List<Notice> findByMeetingIdOrderByCreatedAtDesc(String meetingId);
}
