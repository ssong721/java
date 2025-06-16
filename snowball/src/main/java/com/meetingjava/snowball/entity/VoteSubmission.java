package com.meetingjava.snowball.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class VoteSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date selectedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", referencedColumnName = "vote_id", nullable = false)
    private ScheduleVote vote;

    public VoteSubmission() {}

    // ✅ voteId 없이 vote 객체만 넘김
    public VoteSubmission(ScheduleVote vote, String userName, Date selectedTime) {
        this.vote = vote;
        this.userName = userName;
        this.selectedTime = selectedTime;
    }

    public Long getId() { return id; }
    public String getUserName() { return userName; }
    public Date getSelectedTime() { return selectedTime; }
    public ScheduleVote getVote() { return vote; }

    public void setId(Long id) { this.id = id; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setSelectedTime(Date selectedTime) { this.selectedTime = selectedTime; }
    public void setVote(ScheduleVote vote) { this.vote = vote; }
}
