package meeting_practice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Meeting {
    private final String meetingId;
    private String meetingName;
    private String hostUser;
    private List<String> members;
    private Date meetingStartDate; //일단 schedule(vote) 클래스에서 반환 값 받기 전에는 기본 date로 설정했습니다.
    private Date lastMeetingDate;
    private Date nextMeetingDate;

    public Meeting(String meetingName, String hostUser, Date meetingStartDate) {
        this.meetingId = UUID.randomUUID().toString();  //랜덤으로 고유 아이디 받는 것
        this.meetingName = meetingName;
        this.hostUser = hostUser;
        this.meetingStartDate = meetingStartDate;
        this.members = new ArrayList<>();
        this.addMember(hostUser); //모임장 자동 등록을 하긴 했는데 필요할까요..?
    }

    public void addMember(String userName) {
        if (!members.contains(userName)) { //모임에 추가 후
            members.add(userName);
            System.out.println(userName + " 님이 모임에 추가되었습니다.");
        } else {                           //이미 추가 돼 있는 경우
            System.out.println(userName + " 님은 이미 참여 중입니다."); 
        }
    }

    public void removeMember(String userName) {
        if (!members.contains(userName)) {  //모임에 없는 사람 삭제 하려고 하는 경우
            System.out.println(userName + " 님은 모임에 없습니다.");
            return;
        }
        if (userName.equals(hostUser)) {   //모임장은 삭제 안 되게 했는데 이것도 필요할까요..?
            System.out.println("모임장은 삭제할 수 없습니다.");    
            return;
        }
        members.remove(userName);          //모임에서 삭제 후
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
}
