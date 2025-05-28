package com.meetingjava.snowball.service;

import com.meetingjava.snowball.entity.Meeting;
import com.meetingjava.snowball.repository.MeetingRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    // 추후 findById, findAll 등 추가 가능
}
