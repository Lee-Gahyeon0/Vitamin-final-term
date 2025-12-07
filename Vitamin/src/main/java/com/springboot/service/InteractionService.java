package com.springboot.service;

import com.springboot.domain.IntakeLog;
import com.springboot.domain.InteractionRule;
import com.springboot.domain.Supplement;
import com.springboot.repository.IntakeLogRepository;
import com.springboot.repository.InteractionRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class InteractionService {

    private final IntakeLogRepository intakeLogRepository;
    private final InteractionRuleRepository interactionRuleRepository;

    public InteractionService(IntakeLogRepository intakeLogRepository,
                              InteractionRuleRepository interactionRuleRepository) {
        this.intakeLogRepository = intakeLogRepository;
        this.interactionRuleRepository = interactionRuleRepository;
    }

    /**
     * 오늘 복용한 영양제들 기준으로 상호작용 룰 조회
     * - 오늘 날짜 + taken == true 인 IntakeLog만 사용
     * - 각 영양제의 tags 필드를 콤마로 나눠 태그 추출
     * - 태그 쌍 (tagA, tagB) 조합마다 InteractionRule 조회
     */
    public List<InteractionRule> checkTodayInteractions(Long memberId) {
        LocalDate today = LocalDate.now();

        // 1. 오늘 복용 기록 조회
        List<IntakeLog> todayLogs = intakeLogRepository.findByMemberIdAndDate(memberId, today);

        // 2. taken == true 인 것만 사용 (필드 이름이 isTaken / getTaken 이면 거기에 맞게 수정)
        List<Supplement> takenSupplements = new ArrayList<>();
        for (IntakeLog log : todayLogs) {
            if (Boolean.TRUE.equals(log.isTaken()) && log.getSupplement() != null) {
                takenSupplements.add(log.getSupplement());
            }
        }

        // 3. 모든 태그 수집 (소문자로 정규화)
        Set<String> tags = new HashSet<>();
        for (Supplement s : takenSupplements) {
            String tagStr = s.getTags(); // 필드명이 다르면 수정
            if (tagStr == null || tagStr.isBlank()) continue;

            String[] split = tagStr.split(",");
            for (String t : split) {
                String trimmed = t.trim().toLowerCase();
                if (!trimmed.isEmpty()) {
                    tags.add(trimmed);
                }
            }
        }

        // 태그가 0~1개면 상호작용 쌍이 없으니 바로 리턴
        if (tags.size() < 2) {
            return Collections.emptyList();
        }

        // 4. 태그 쌍(조합) 만들기
        List<String> tagList = new ArrayList<>(tags);
        List<InteractionRule> results = new ArrayList<>();

        for (int i = 0; i < tagList.size(); i++) {
            for (int j = i + 1; j < tagList.size(); j++) {
                String a = tagList.get(i);
                String b = tagList.get(j);

                // 여기서 repo 메서드 이름/리턴타입은 네 구현에 맞게 살짝 수정해도 됨
                // 예: Optional<InteractionRule> 이면 isPresent 체크, List면 isEmpty 체크
                List<InteractionRule> rulesAB =
                        interactionRuleRepository.findByTagAAndTagB(a, b);
                List<InteractionRule> rulesBA =
                        interactionRuleRepository.findByTagAAndTagB(b, a);

                if (rulesAB != null) {
                    results.addAll(rulesAB);
                }
                if (rulesBA != null) {
                    results.addAll(rulesBA);
                }
            }
        }

        // 같은 룰이 중복으로 들어갈 수 있으므로 중복 제거 (id 기준)
        Map<Long, InteractionRule> unique = new LinkedHashMap<>();
        for (InteractionRule r : results) {
            if (r.getId() != null) {
                unique.put(r.getId(), r);
            }
        }

        return new ArrayList<>(unique.values());
    }
}
