package com.springboot.repository;

import com.springboot.domain.RawIndividualIngredient;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

// 원료정보(raw_individual_ingredient)에 접근하는 레포지토리
public interface RawIndividualIngredientRepository
extends JpaRepository<RawIndividualIngredient, Long> {
	
	// 인정번호로 검색
    List<RawIndividualIngredient> findByApprovalNo(String approvalNo);
    
    // 원재료명에 특정 단어가 들어간 거 검색
    List<RawIndividualIngredient> findByRawMaterialNameContaining(String keyword);
}