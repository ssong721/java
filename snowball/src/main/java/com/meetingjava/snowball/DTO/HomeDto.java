package com.meetingjava.snowball.dto;

import java.util.Date;

public class HomeDto {
    private String id;
    private String name;
    private int memberCount;
    private Date day;
    private boolean isManager;

    // Getter/Setter 필수!
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public Date getDayAndTime() { return day; }
    public void setDayAndTime(Date day) { this.day = day; }

    public boolean isManager() { return isManager; }
    public void setIsManager(boolean isManager) { this.isManager = isManager; }
} 
