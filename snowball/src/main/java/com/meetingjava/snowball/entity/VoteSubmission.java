package com.meetingjava.snowball.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class VoteSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String voteId;
    private String userName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date selectedTime;

    public VoteSubmission() {}

    public VoteSubmission(String voteId, String userName, Date selectedTime) {
        this.voteId = voteId;
        this.userName = userName;
        this.selectedTime = selectedTime;
    }

    public Long getId() { return id; }
    public String getVoteId() { return voteId; }
    public String getUserName() { return userName; }
    public Date getSelectedTime() { return selectedTime; }

    public void setId(Long id) { this.id = id; }
    public void setVoteId(String voteId) { this.voteId = voteId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setSelectedTime(Date selectedTime) { this.selectedTime = selectedTime; }
}
