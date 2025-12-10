package com.springboot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.springboot.domain.RawProduct;

@Service
public class CategoryRuleService {

    public List<String> extractTags(RawProduct raw) {
        List<String> result = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        if (raw.getProductName() != null) sb.append(raw.getProductName()).append(" ");
        if (raw.getMainFunction() != null) sb.append(raw.getMainFunction()).append(" ");
        if (raw.getRawMaterialsText() != null) sb.append(raw.getRawMaterialsText()).append(" ");

        String text = sb.toString().toLowerCase();   // 영어는 소문자 비교

        // ====== 한글/영어 둘 다 조금씩 커버 ======
        if (containsAny(text, "철분", "iron", "ferrous")) {
            result.add("철분");
        }
        if (containsAny(text, "칼슘", "calcium")) {
            result.add("칼슘");
        }
        if (containsAny(text, "비타민 c", "vitamin c", "ascorbic")) {
            result.add("비타민C");
        }
        if (containsAny(text, "비타민 b", "vitamin b")) {
            result.add("비타민B");
        }
        if (containsAny(text, "오메가3", "omega-3", "omega 3", "epa", "dha")) {
            result.add("오메가3");
        }
        if (containsAny(text, "유산균", "프로바이오틱", "probiotic")) {
            result.add("유산균");
        }
        if (containsAny(text, "홍삼", "red ginseng")) {
            result.add("홍삼");
        }
        if (containsAny(text, "루테인", "lutein")) {
            result.add("루테인");
        }
        if (containsAny(text, "멀티비타민", "multivitamin", "multi vitamin")) {
            result.add("멀티비타민");
        }

        return result;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String k : keywords) {
            if (text.contains(k.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
