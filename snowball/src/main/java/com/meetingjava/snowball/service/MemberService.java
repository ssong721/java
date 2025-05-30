package com.meetingjava.snowball.service;

import com.meetingjava.snowball.entity.Member;
import com.meetingjava.snowball.entity.Meeting;
import com.meetingjava.snowball.entity.User;
import com.meetingjava.snowball.entity.Role;
import com.meetingjava.snowball.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void joinMeeting(User user, Meeting meeting) {
        // 이미 참여 중인지 확인
        Optional<Member> existing = memberRepository.findByUserAndMeeting(user, meeting);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 해당 모임에 참여 중입니다.");
        }

        // 새 멤버 생성
        Member member = new Member(user, meeting, Role.MEMBER);
        memberRepository.save(member);
    }

    @Transactional
    public void leaveMeeting(User user, Meeting meeting) {
        Member member = memberRepository.findByUserAndMeeting(user, meeting)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임에 참여하지 않았습니다."));
        
        if (member.getRole() == Role.HOST) {
            throw new IllegalStateException("모임장은 탈퇴할 수 없습니다.");
        }

        memberRepository.delete(member);
    }

    public boolean isUserInMeeting(User user, Meeting meeting) {
        return memberRepository.findByUserAndMeeting(user, meeting).isPresent();
    }
}
