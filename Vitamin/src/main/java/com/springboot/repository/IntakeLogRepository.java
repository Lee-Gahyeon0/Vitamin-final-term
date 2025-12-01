package com.springboot.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.domain.IntakeLog;

//복용기록(intake)에 접근하는 레포지토리
public interface IntakeLogRepository extends JpaRepository<IntakeLog, Long> {
	
	// 특정 날짜에 특정 회원의 기록 조회
	List<IntakeLog> findByMemberIdAndDate(Long memberID, LocalDate date);
	
	// 회원 기록 전체를 날짜 순으로 정렬?보기?
	List<IntakeLog> findByMemberIdOrderByDateDesc(Long memberID);
	
}
