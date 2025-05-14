package com.meetingjava.snowball.entity;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Feedback {

    private Member member;
    private String content;
    private LocalDate submittedAt;
}
