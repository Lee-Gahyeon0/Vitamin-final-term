package com.springboot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter  
@NoArgsConstructor
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이메일
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    // 비번
    @Column(nullable = false, length = 255)
    private String password;

    // 닉네임
    @Column(nullable = false, length = 100)
    private String nickname;
    
    // 역할- 기본 USER
    @Column(length = 20, nullable = false)
    private String role = "USER";  

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

}
