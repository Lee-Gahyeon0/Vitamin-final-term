package com.springboot.service;

import com.springboot.domain.NormalizedSupplement;
import com.springboot.domain.RawProduct;
import com.springboot.repository.NormalizedSupplementRepository;
import com.springboot.repository.RawProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RawProductNormalizationService {

    private final RawProductRepository rawProductRepository;
    private final NormalizedSupplementRepository normalizedSupplementRepository;
    private final CategoryRuleService categoryRuleService;

    public RawProductNormalizationService(RawProductRepository rawProductRepository,
                                          NormalizedSupplementRepository normalizedSupplementRepository,
                                          CategoryRuleService categoryRuleService) {
        this.rawProductRepository = rawProductRepository;
        this.normalizedSupplementRepository = normalizedSupplementRepository;
        this.categoryRuleService = categoryRuleService;
    }

    @Transactional
    public void normalizeAll() {

        // 0) 이전 정제 데이터 싹 비우고 시작
        normalizedSupplementRepository.deleteAll();

        // 1) 원본 전체 조회
        List<RawProduct> all = rawProductRepository.findAll();

        for (RawProduct raw : all) {

            // 1차 필터: 제형으로 필터링
            String form = raw.getFormType();
            if (form == null) continue;

            String f = form.toLowerCase();
            boolean looksLikeSupplementForm =
                    f.contains("정제") || f.contains("캡슐") || f.contains("환")
                 || f.contains("분말") || f.contains("젤리") || f.contains("액상")
                 || f.contains("연질캡슐") || f.contains("액상캡슐");

            if (!looksLikeSupplementForm) {
                continue;
            }

            // 2차: 텍스트에서 태그 뽑기
            var tags = categoryRuleService.extractTags(raw);
            if (tags.isEmpty()) {
                continue;
            }

            // 3) 정제 테이블로 저장
            NormalizedSupplement ns = new NormalizedSupplement();
            ns.setRawProductId(raw.getId());
            ns.setProductName(raw.getProductName());
            ns.setCompanyName(raw.getCompanyName());
            ns.setFormType(raw.getFormType());
            ns.setMainFunction(raw.getMainFunction());
            ns.setTags(String.join(",", tags));  // "철분,칼슘"

            normalizedSupplementRepository.save(ns);
        }
    }
}

