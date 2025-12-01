package com.springboot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "raw_individual_ingredient")
@Getter
@Setter
@NoArgsConstructor
public class RawIndividualIngredient {

    // PK: 자동 증가 ID (RAW 레코드 고유 번호)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 원료인정번호 (HF_FNCLTY_MTRAL_RCOGN_NO)
    // 식약처에서 개별인정형 원료마다 부여하는 고유 번호
    @Column(name = "approval_no")
    private String approvalNo;

    // 원재료 이름 (RAWMTRL_NM)
    // 예: "홍삼농축액", "비타민C" 등
    @Column(name = "raw_material_name")
    private String rawMaterialName;

    // 1일 섭취량 하한 (DAY_INTK_LOWLIMIT)
    // 문자열 그대로 저장 (숫자+단위 분리는 나중에 정제 단계에서)
    @Column(name = "daily_min")
    private String dailyMin;

    // 1일 섭취량 상한 (DAY_INTK_HIGHLIMIT)
    @Column(name = "daily_max")
    private String dailyMax;

    // 중량 단위 (WT_UNIT)
    // 예: "mg", "g" 등
    @Column(name = "unit")
    private String unit;

    // 주된 기능성 (PRIMARY_FNCLTY)
    // 예: "면역력 증진에 도움을 줄 수 있음" 
    @Column(name = "main_function", columnDefinition = "TEXT")
    private String mainFunction;

    // 섭취 시 주의사항 (IFTKN_ATNT_MATR_CN)
    // 부작용, 복용 주의사항 등 
    @Column(name = "caution_text", columnDefinition = "TEXT")
    private String cautionText;

    // 이 row 전체를 JSON 문자열 그대로 백업해 두는 컬럼
    @Column(name = "raw_json", columnDefinition = "LONGTEXT")
    private String rawJson;

    // 레코드가 DB에 저장된 시각
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
}
