package com.springboot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter  
@NoArgsConstructor
@Entity
@Table(name = "intake_log")
public class IntakeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK: member.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // FK: supplement.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplement_id", nullable = false)
    private Supplement supplement;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    // "MORNING", "NOON", "EVENING" 같은 문자열
    @Column(name = "time_slot", nullable = false, length = 20)
    private String timeSlot;

    // 1 = 먹음, 0 = 안 먹음
    @Column(nullable = false)
    private boolean taken;

    @Column(length = 1000)
    private String memo;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

 }
