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

    public Meeting createMeeting(String meetingName, String hostUser, Date startDate) {
        Meeting meeting = new Meeting(meetingName, hostUser, startDate);
        return meetingRepository.save(meeting);
    }

    // 추후 findById, findAll 등 추가 가능
}
