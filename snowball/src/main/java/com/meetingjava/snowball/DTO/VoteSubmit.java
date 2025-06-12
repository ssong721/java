package com.meetingjava.snowball.dto;

import java.util.List;

public class VoteSubmit {

    private String user;
    private Long userId;
    private List<String> selectedTimes;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getSelectedTimes() {
        return selectedTimes;
    }

    public void setSelectedTimes(List<String> selectedTimes) {
        this.selectedTimes = selectedTimes;
    }
}
