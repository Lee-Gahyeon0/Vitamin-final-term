package com.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.domain.Member;



//회원 테이블(member)에 접근하는 레포지토리
public interface MemberRepository extends JpaRepository<Member, Long> {
	
	// 로그인용 : 이메일 기준 회원 찾기
	Member findByEmail(String email);
	
}
