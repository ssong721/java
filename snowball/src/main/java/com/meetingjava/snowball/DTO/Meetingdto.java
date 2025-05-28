package com.meetingjava.snowball.dto;

import lombok.Data;
import java.util.Date;

@Data
public class Meetingdto {
    private String name;
    private String host;
    private Date startDate;
}
