package com.meetingjava.snowball.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.meetingjava.snowball.entity.User;
import com.meetingjava.snowball.entity.Role;

import java.util.Collection;
import java.util.ArrayList;

public class CustomUserDetails implements UserDetails {

    private final User user; // 여러분이 만든 User 엔티티

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        if (!user.getMemberList().isEmpty()) {
            Role role = user.getMemberList().get(0).getRole();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name())); // 또는 role.toString()
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_GUEST"));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 나머지 메서드는 기본값으로 true 반환
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public User getUser() { return user; } // 추가 getter
}
