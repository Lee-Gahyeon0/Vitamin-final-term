package com.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.domain.InteractionRule;


//영양제 조합 위험 규칙(interaction_rule)에 접근하는 레포지토리
public interface InteractionRuleRepository extends JpaRepository<InteractionRule, Long> {
	
	 // 태그 A,B 조합으로 규칙 검색
    List<InteractionRule> findByTagAAndTagB(String tagA, String tagB);

    // 태그 이름으로 검색
    List<InteractionRule> findByTagAOrTagB(String tag, String tag2);
}
