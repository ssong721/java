package com.meetingjava.snowball.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class LogoutRequest {
    private String username;
    private String token;
    // getters/setters
}
