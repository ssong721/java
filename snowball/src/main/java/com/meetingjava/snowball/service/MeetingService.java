package com.meetingjava.snowball.service;

import com.meetingjava.snowball.dto.HomeDto;
import com.meetingjava.snowball.entity.Member;
import com.meetingjava.snowball.entity.Meeting;
import com.meetingjava.snowball.entity.User;
import com.meetingjava.snowball.repository.MeetingRepository;
import com.meetingjava.snowball.repository.UserRepository;
import com.meetingjava.snowball.repository.MemberRepository;
import org.springframework.stereotype.Service;
import com.meetingjava.snowball.entity.Role;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    public MeetingService(MeetingRepository meetingRepository, UserRepository userRepository, MemberRepository memberRepository) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
    }

    public Meeting createMeeting(String meetingName, String hostUsername) {
        Date now = new Date();
        Meeting meeting = new Meeting(meetingName, hostUsername, now);
        meetingRepository.save(meeting);

        User user = userRepository.findByUsername(hostUsername)
        .orElseThrow(() -> new RuntimeException("유저 없음"));

        Member hostMember = new Member(user, meeting, Role.HOST);
        memberRepository.save(hostMember);

        return meeting;
    }

    public Meeting findById(String meetingId) {
        return meetingRepository.findById(meetingId)
                .orElse(null);
    }

    public String getMeetingNameById(String meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("해당 meetingId 없음: " + meetingId))
                .getMeetingName();
    }

    public List<HomeDto> getHomesForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        List<HomeDto> homes = new ArrayList<>();

        for (Member member : user.getMemberList()) {
            Meeting meeting = member.getMeeting();

            HomeDto dto = new HomeDto();
            dto.setId(meeting.getMeetingId());
            dto.setName(meeting.getMeetingName());
            dto.setDayAndTime(meeting.getMeetingStartDate());
            dto.setMemberCount(memberRepository.countByMeeting(meeting));
            dto.setIsManager(meeting.getHostUser().equals(username));

            homes.add(dto);
        }

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
                dto.setIsManager(true);

                homes.add(dto);
            }
        }

        return homes;
    }
} 
