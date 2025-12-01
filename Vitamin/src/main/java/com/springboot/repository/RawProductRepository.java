package com.springboot.repository;

import com.springboot.domain.RawProduct;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


//제품 테이블(raw_product)에 접근하는 레포지토리
public interface RawProductRepository
extends JpaRepository<RawProduct, Long> {
	
	// 제품 코드(품목제조번호)로 검색
    List<RawProduct> findByProductCode(String productCode);

    // 제품명에 키워드가 포함된 것 검색 (LIKE %키워드%)
    List<RawProduct> findByProductNameContaining(String keyword);

    // 회사 이름으로 검색
    List<RawProduct> findByCompanyNameContaining(String companyName);
}
