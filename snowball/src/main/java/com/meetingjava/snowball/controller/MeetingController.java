package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.Meeting;
import com.meetingjava.snowball.service.MeetingService;
import com.meetingjava.snowball.dto.Meetingdto;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    // http://localhost:8080/meetings?name=OO&host=OO&startDate=2024-06-01
    @PostMapping
    public Meeting createMeeting(@RequestParam String name,
            @RequestParam String host,
            @RequestParam Date startDate) {
        return meetingService.createMeeting(name, host, startDate);
    }
}
