package com.meetingjava.snowball.service;

import com.meetingjava.snowball.dto.HomeDto;
import com.meetingjava.snowball.entity.Meeting;
import com.meetingjava.snowball.repository.MeetingRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;

    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public Meeting createMeeting(String meetingName, String hostUsername) {
        Date now = new Date(); // 현재 시간

        Meeting meeting = new Meeting(meetingName, hostUsername, now);
        return meetingRepository.save(meeting);
    }

    public List<HomeDto> getHomesForUser(String username) {
        List<Meeting> meetings = meetingRepository.findByHostUsername(username);
        return meetings.stream().map(meeting -> {
            HomeDto dto = new HomeDto();
            dto.setName(meeting.getName());
            dto.setMemberCount(meeting.getMembers().size());
            dto.setDayAndTime(meeting.getDay() + " " + meeting.getTime());
            dto.setIsManager(meeting.getHost().getUsername().equals(username));
            return dto;
        }).collect(Collectors.toList());
    }


    // 추후 findById, findAll 등 추가 가능
}
