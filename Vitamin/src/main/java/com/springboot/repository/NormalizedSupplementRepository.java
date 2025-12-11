package com.springboot.repository;

import com.springboot.domain.NormalizedSupplement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NormalizedSupplementRepository
        extends JpaRepository<NormalizedSupplement, Long> {

    // 제품명 검색
    List<NormalizedSupplement> findByProductNameContaining(String keyword);

    // 회사명 검색
    List<NormalizedSupplement> findByCompanyNameContaining(String keyword);

    // 태그
    List<NormalizedSupplement> findByTagsContaining(String tag);

    // 자동 연결용: 제품명 + 회사명 둘 다 포함되는 항목 찾기
    List<NormalizedSupplement> findByProductNameContainingAndCompanyNameContaining(
            String productName,
            String companyName
    );
}