package com.meetingjava.snowball.service;

import com.meetingjava.snowball.dto.HomeDto;
import com.meetingjava.snowball.repository.MeetingRepository;
import com.meetingjava.snowball.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.meetingjava.snowball.entity.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository; // 추가

    public MeetingService(MeetingRepository meetingRepository, UserRepository userRepository) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
    }


    public Meeting createMeeting(String meetingName, String hostUsername) {
        Date now = new Date(); // 현재 시간

        Meeting meeting = new Meeting(meetingName, hostUsername, now);
        return meetingRepository.save(meeting);
    }

    public Meeting findById(String meetingId) {
        return meetingRepository.findById(meetingId)
                .orElse(null);  // 없으면 null 반환 (원하는 대로 수정 가능)
    }

    public List<HomeDto> getHomesForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        List<HomeDto> homes = new ArrayList<>();

        // ✅ 1. 멤버로 참여한 모임들
        for (Member member : user.getMemberList()) {
            Meeting meeting = member.getMeeting();

            HomeDto dto = new HomeDto();
            dto.setId(meeting.getMeetingId());
            dto.setName(meeting.getMeetingName());
            dto.setDayAndTime(meeting.getMeetingStartDate());
            dto.setMemberCount(meeting.getMembers().size());
            dto.setIsManager(meeting.getHostUser().equals(username)); // 이건 false일 것

            homes.add(dto);
        }

        // ✅ 2. 호스트로 만든 모임들 (이미 멤버로 포함된 경우 중복 제거)
        List<Meeting> hostedMeetings = meetingRepository.findByHostUser(username);
        for (Meeting meeting : hostedMeetings) {
            boolean alreadyAdded = homes.stream()
                    .anyMatch(dto -> dto.getId().equals(meeting.getMeetingId()));
            if (!alreadyAdded) {
                HomeDto dto = new HomeDto();
                dto.setId(meeting.getMeetingId());
                dto.setName(meeting.getMeetingName());
                dto.setDayAndTime(meeting.getMeetingStartDate());
                dto.setMemberCount(meeting.getMembers().size());
                dto.setIsManager(true); // ✅ 호스트니까 true로 설정

                homes.add(dto);
            }
        }

        return homes;
    }

    // 추후 findById, findAll 등 추가 가능
}
