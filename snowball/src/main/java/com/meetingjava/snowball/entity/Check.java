package com.meetingjava.snowball.entity;

import org.springframework.stereotype.Component;

@Component
public class Check {

    private boolean enable;
    private String question;
    private String answer;
    private String method;
    private double rate;

    public void checkOn() {
        this.enable = true;
    }

    public void checkOff() {
        this.enable = false;
    }

    public boolean checkCode(String userAnswer) {
        return userAnswer != null && userAnswer.equals(this.answer);
    }

    public void checkSet(String question, String method) {
        this.question = question;
        this.method = method;
    }

    public void checkRun(String userAnswer) {
        if (!enable) {
            System.out.println("출석이 아직 활성화되지 않았습니다.");
            return;
        }

        this.answer = userAnswer;
        System.out.println("출석 처리 완료 (방식: " + method + ", 답변: " + userAnswer + ")");
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getQuestion() {
        return question;
    }

    public void debugPrint() {
        System.out.println("Check class real updated");
    }
}

