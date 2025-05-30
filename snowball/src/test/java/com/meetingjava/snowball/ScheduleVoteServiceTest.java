package com.meetingjava.snowball;

import com.meetingjava.snowball.entity.ScheduleVote;
import com.meetingjava.snowball.service.ScheduleVoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
public class ScheduleVoteServiceTest {

    @Autowired
    private ScheduleVoteService voteService;

    @Test
    public void createVoteTest() throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date start = format.parse("2025-06-01 10:00");
        Date end = format.parse("2025-06-03 22:00");

        ScheduleVote vote = voteService.createVote(start, end, 120, 1L);

        System.out.println("✅ 생성된 voteId: " + vote.getVoteId());
    }
}
