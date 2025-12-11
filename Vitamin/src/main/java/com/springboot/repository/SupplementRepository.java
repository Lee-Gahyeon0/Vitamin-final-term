package com.springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.domain.Supplement;


//사용자 개인 영양제(supplement)에 접근하는 레포지토리
public interface SupplementRepository extends JpaRepository<Supplement, Long> {
	
	// 현재 먹는 영양제 목록
	List<Supplement> findByMemberIdAndDeletedFalse(Long memberId);


	Optional<Supplement> findByIdAndDeletedFalse(Long id);
}    
