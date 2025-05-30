package com.meetingjava.snowball.service;

import com.meetingjava.snowball.repository.NoticeRepository;
import com.meetingjava.snowball.entity.Notice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    // meetingId로 최신 공지 제목 반환 (없으면 기본 문구)
    public String getLatestNoticeTitle(String meetingId) {
        List<Notice> notices = noticeRepository.findByMeetingIdOrderByCreatedAtDesc(meetingId);
        if (notices.isEmpty()) {
            return "공지사항이 없습니다.";
        }
        return notices.get(0).getTitle();
    }

    // 공지사항 저장, 수정, 삭제 기능도 추가 가능
}

