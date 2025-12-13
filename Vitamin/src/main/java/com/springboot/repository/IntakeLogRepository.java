package com.springboot.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.domain.IntakeLog;

//복용기록(intake)에 접근하는 레포지토리
public interface IntakeLogRepository extends JpaRepository<IntakeLog, Long> {
	
	// 회원 전체 복용 기록
    List<IntakeLog> findByMemberIdOrderByDateDesc(Long memberId);
 
    // 회원의 특적 날짜 복용 기록
    List<IntakeLog> findByMemberIdAndDate(Long memberId, LocalDate date);
    
    //삭제
    long deleteByIdAndMemberId(Long id, Long memberId);
	
}
