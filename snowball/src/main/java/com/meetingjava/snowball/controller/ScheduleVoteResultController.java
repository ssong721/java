package com.meetingjava.snowball.controller;

import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.service.ScheduleVoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ScheduleVoteResultController {

    private final ScheduleVoteService scheduleVoteService;

    @GetMapping("/schedulevote/result/{meetingId}")
    public String showVoteResult(@PathVariable String meetingId, Model model) {
        ScheduleVote vote = scheduleVoteService.findByMeetingId(meetingId);

        // 시간별 인원 수 카운팅
        Map<String, Integer> timeCounts = new HashMap<>();
        for (List<Date> userVotes : vote.getVotes().values()) {
            for (Date date : userVotes) {
                String key = String.format("%tA %tH시", date, date); // 예: "Tuesday 18시"
                timeCounts.put(key, timeCounts.getOrDefault(key, 0) + 1);
            }
        }

        model.addAttribute("meetingId", meetingId);
        model.addAttribute("voteId", vote.getVoteId());
        model.addAttribute("meetingName", vote.getMeetingId());
        model.addAttribute("timeCounts", timeCounts);

        return "schedulevote-result";
    }
}