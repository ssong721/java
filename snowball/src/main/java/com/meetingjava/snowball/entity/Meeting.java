package com.meetingjava.snowball.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity // 이 클래스가 DB 테이블로 등록되도록 설정
public class Meeting {

    @Id
    private String meetingId;

    private String meetingName;
    private String hostUser;

    @ElementCollection // List도 JPA가 저장할 수 있도록 설정
    private List<String> members;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> memberList = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date meetingStartDate; //일단 schedule(vote) 클래스에서 반환 값 받기 전에는 기본 date로 설정했습니다.

    private Date lastMeetingDate;
    private Date nextMeetingDate;

    // ⚠️ JPA를 위한 기본 생성자 필수!
    public Meeting() {
        this.meetingId = UUID.randomUUID().toString();
        this.members = new ArrayList<>();
    }

    public Meeting(String meetingName, String hostUser, Date meetingStartDate) {
        this(); // 기본 생성자 호출
        this.meetingName = meetingName;
        this.hostUser = hostUser;
        this.meetingStartDate = meetingStartDate;
        this.addMember(hostUser); // 모임장 자동 등록
    }

    public void addMember(String userName) {
        if (!members.contains(userName)) {
            members.add(userName);
            System.out.println(userName + " 님이 모임에 추가되었습니다.");
        } else {
            System.out.println(userName + " 님은 이미 참여 중입니다.");
        }
    }

    public void removeMember(String userName) {
        if (!members.contains(userName)) {
            System.out.println(userName + " 님은 모임에 없습니다.");
            return;
        }
        if (userName.equals(hostUser)) {
            System.out.println("모임장은 삭제할 수 없습니다.");
            return;
        }
        members.remove(userName);
        System.out.println(userName + " 님이 모임에서 삭제되었습니다.");
    }

    public void setLastMeetingDate(Date date) {
        this.lastMeetingDate = date;
    }

    public void setNextMeetingDate(Date date) {
        this.nextMeetingDate = date;
    }

    public void printMeetingInfo() {
        System.out.println("\n=== 모임 정보 ===");
        System.out.println("ID: " + meetingId);
        System.out.println("이름: " + meetingName);
        System.out.println("모임장: " + hostUser);
        System.out.println("시작일: " + meetingStartDate);
        System.out.println("마지막 모임: " + (lastMeetingDate != null ? lastMeetingDate : "없음"));
        System.out.println("다음 모임: " + (nextMeetingDate != null ? nextMeetingDate : "없음"));
        System.out.println("참여자 목록: " + members);
    }

    // ✅ Getter 추가 (JPA & JSON 변환 시 필요)
    public String getMeetingId() {
        return meetingId;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public String getHostUser() {
        return hostUser;
    }

    public List<String> getMembers() {
        return members;
    }

    public List<Member> getMemberList() {
        return memberList;
    }

    public Date getMeetingStartDate() {
        return meetingStartDate;
    }

    public Date getLastMeetingDate() {
        return lastMeetingDate;
    }

    public Date getNextMeetingDate() {
        return nextMeetingDate;
    }

    // Setter도 필요한 만큼 추가 가능 (JPA나 컨트롤러에서 필요 시)
}
