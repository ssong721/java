package com.meetingjava.snowball.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "check_quiz") 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Check {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String meetingId;

    private String question;

    private String answer;

    private boolean enable;

    private String method;

    private double rate;
}



