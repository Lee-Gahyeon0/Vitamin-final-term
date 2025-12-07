package com.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.domain.Supplement;


//사용자 개인 영양제(supplement)에 접근하는 레포지토리
public interface SupplementRepository extends JpaRepository<Supplement, Long> {

    // 특정 회원의 영양제 목록 조회
    List<Supplement> findByMemberId(Long memberId);
}