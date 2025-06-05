package com.meetingjava.snowball.entity;

import java.io.Serializable;
import java.util.ArrayList;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "user")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Member> memberList = new ArrayList<>();

    @OneToMany(mappedBy = "hostUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meeting> hostedMeetings = new ArrayList<>();
}
