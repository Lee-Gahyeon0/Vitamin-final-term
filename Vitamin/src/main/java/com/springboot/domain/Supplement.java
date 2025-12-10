package com.springboot.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "supplement")
public class Supplement {

    // PK: 영양제 고유 ID (자동 증가)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK: 이 영양제를 등록한 사용자 
    // 한 명의 Member가 여러 Supplement를 가질 수 있음 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 영양제 이름 (필수) 
    @Column(nullable = false, length = 255)
    private String name;

    // 브랜드명 
    @Column(length = 255)
    private String brand;

    // 이미지 파일 경로 또는 URL 
    @Column(name = "image_path", length = 500)
    private String imagePath;

    // 태그 문자열 
    @Column(length = 500)
    private String tags;

    // 자유 메모 (선택)
    @Column(length = 1000)
    private String memo;

    // 어떤 "정제된(normalized)" 제품 기반 영양제인가
    // FK: normalized_supplement.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "normalized_supplement_id")
    private NormalizedSupplement normalizedSupplement;

    // 레코드 생성 시각 
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
