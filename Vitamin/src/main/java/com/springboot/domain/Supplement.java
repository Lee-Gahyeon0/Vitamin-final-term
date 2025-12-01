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

    // FK: 이 영양제를 등록한 사용자 (member 테이블의 id 참조)
    // 한 명의 Member가 여러 Supplement를 가질 수 있음 (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 영양제 이름 (필수) 예: "비타민 C 1000", "오메가3"
    @Column(nullable = false, length = 255)
    private String name;

    // 브랜드명 (선택) 예: "종근당", "NOW", "뉴트리코어"
    @Column(length = 255)
    private String brand;

    // 이미지 파일 경로 또는 URL (선택)
    @Column(name = "image_path", length = 500)
    private String imagePath;

    // 태그 문자열 (선택) 예: "철분,비타민C,피로회복" 처럼 콤마로 구분해서 저장
    @Column(length = 500)
    private String tags;

    // 자유 메모 (선택)
    @Column(length = 1000)
    private String memo;
    
    
    // 이 영양제가 어떤 식약처 제품 기반인지(선택)
    // FK: raw_product.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_product_id")
    private RawProduct rawProduct;

    // 레코드 생성 시각 (DB에서 DEFAULT CURRENT_TIMESTAMP로 채우는 컬럼 가정)
    // 애플리케이션에서는 값 직접 안 넣으므로 insertable=false, updatable=false
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
