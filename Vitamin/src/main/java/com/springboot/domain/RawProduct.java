package com.springboot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "raw_product")
@Getter
@Setter
@NoArgsConstructor
public class RawProduct {

    // 내부 PK (자동증가) 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 인허가번호 (LCNS_NO) 
    @Column(name = "license_no")
    private String licenseNo;

    // 품목제조번호 (PRDLST_REPORT_NO)
    @Column(name = "product_code")
    private String productCode;

    // 품목명 (PRDLST_NM) 
    @Column(name = "product_name")
    private String productName;

    // 업소명 (BSSH_NM) 
    @Column(name = "company_name")
    private String companyName;

    // 보고일자 (PRMS_DT, YYYYMMDD 형태 문자열)
    @Column(name = "report_date")
    private String reportDate;

    //소비기한/유통기간 관련 정보 (POG_DAYCNT)
    @Column(name = "expire_date", columnDefinition = "TEXT")
    private String expireDate;

    // 제품형태 (PRDT_SHAP_CD_NM, 예: 정제, 캡슐 등) 
    @Column(name = "form_type")
    private String formType;

    // 섭취방법 (NTK_MTHD) – 문장이 길 수 있어 TEXT 
    @Column(name = "intake_method", columnDefinition = "TEXT")
    private String intakeMethod;

    // 주된 기능성 (PRIMARY_FNCLTY) – 여러 문장이 올 수 있어서 TEXT 
    @Column(name = "main_function", columnDefinition = "TEXT")
    private String mainFunction;

    // 원재료 전체 문자열 (RAWMTRL_NM) 
    @Column(name = "raw_materials_text", columnDefinition = "TEXT")
    private String rawMaterialsText;

    // 섭취 시 주의사항 (IFTKN_ATNT_MATR_CN) 
    @Column(name = "caution_text", columnDefinition = "TEXT")
    private String cautionText;

    
     // 이 row 전체의 원본 JSON 문자열
     // (나중에 문제 생기면 역추적/디버깅용)
    @Column(name = "raw_json", columnDefinition = "LONGTEXT")
    private String rawJson;

    // 레코드 생성 시작
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
}
