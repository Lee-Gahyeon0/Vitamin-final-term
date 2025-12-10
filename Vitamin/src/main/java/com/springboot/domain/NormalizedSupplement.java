package com.springboot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "normalized_supplement")
@Getter
@Setter
@NoArgsConstructor
public class NormalizedSupplement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 원본 raw_product의 id (FK 개념)
    @Column(name = "raw_product_id")
    private Long rawProductId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "form_type")
    private String formType;

    @Column(name = "main_function", columnDefinition = "TEXT")
    private String mainFunction;

    // "철분,칼슘" 이런 식의 태그 문자열
    @Column(name = "tags")
    private String tags;
}
