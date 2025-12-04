package com.springboot.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import com.springboot.domain.RawProduct;
import com.springboot.domain.RawIndividualIngredient;
import com.springboot.repository.RawIndividualIngredientRepository;
import com.springboot.repository.RawProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



/**
 * 식약처 API를 호출해서 데이터를 가져오고,
 * 가져온 결과(JSON)를 프로젝트의 RAW 테이블에 저장하는 서비스 클래스.
 */

@Service
public class FoodSafetyApiService {

    // JPA로 DB 접근하기 위한 레포지토리들
    private final RawProductRepository rawProductRepository;
    private final RawIndividualIngredientRepository rawIndividualIngredientRepository;

    // JSON 파싱 도구
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 공통 base URL (keyId까지)
    // 설정에서 가져올 값들
    @Value("${foodsafety.api.host}")
    private String apiHost;   // http://openapi.foodsafetykorea.go.kr/api

    @Value("${foodsafety.api.key}")
    private String apiKey;    
    
    // 한 번에 가져올 개수
    private static final int PRODUCT_PAGE_SIZE = 1000;   // 품목제조신고(C003)
    private static final int INGREDIENT_PAGE_SIZE = 200; // 개별인정형(I-0050)

    public FoodSafetyApiService(
            RawProductRepository rawProductRepository,
            RawIndividualIngredientRepository rawIndividualIngredientRepository
    ) {
        this.rawProductRepository = rawProductRepository;
        this.rawIndividualIngredientRepository = rawIndividualIngredientRepository;
    }

    /**
     * 1) 식약처 "건강기능식품 품목제조신고(C003)" API를 호출해
     *    JSON의 row 배열을 돌면서 raw_product 테이블에 여러 건 저장.
     */
    public void loadProductDataFromApi() {
    	
    	long count = rawProductRepository.count(); //이미 있으면 안가져오기
        if (count > 0) {
            System.out.println("[품목제조신고] raw_product에 이미 " + count + "건 있음 -> API 호출 안 함");
            return;
        }
        
        RestTemplate restTemplate = new RestTemplate();

        int startIdx = 1;
        int totalSaved = 0;

        while (true) {
            int endIdx = startIdx + PRODUCT_PAGE_SIZE - 1;

            // http://openapi.../키/C003/json/startIdx/endIdx
            String url = apiHost + "/" + apiKey + "/C003/json/" + startIdx + "/" + endIdx;

            System.out.println("[품목제조신고] 요청: " + url);

            ResponseEntity<String> response =
                    restTemplate.getForEntity(url, String.class);

            // 응답 body 전체(JSON 문자열)
            String json = response.getBody();
            if (json == null || json.isEmpty()) {
                System.out.println("[품목제조신고] 응답이 비어있음 -> 종료");
                break;
            }

            // HTML/스크립트 에러 페이지가 들어온 경우 방어
            if (json.trim().startsWith("<")) {
                System.out.println("[품목제조신고] JSON 아님 (스크립트/HTML 응답) -> 종료");
                break;
            }

            try {
                JsonNode root = objectMapper.readTree(json);
                JsonNode c003 = root.path("C003");
                JsonNode rows = c003.path("row");

                if (!rows.isArray() || rows.size() == 0) {
                    System.out.println("[품목제조신고] 더 이상 row 없음 -> 종료");
                    break;
                }

                int pageSaved = 0;
                for (JsonNode item : rows) {
                    RawProduct p = new RawProduct();

                    p.setLicenseNo(item.path("LCNS_NO").asText(null));              // 인허가번호
                    p.setProductCode(item.path("PRDLST_REPORT_NO").asText(null));   // 품목제조번호
                    p.setProductName(item.path("PRDLST_NM").asText(null));          // 품목명
                    p.setCompanyName(item.path("BSSH_NM").asText(null));            // 업소명
                    p.setReportDate(item.path("PRMS_DT").asText(null));             // 보고일자
                    p.setExpireDate(item.path("POG_DAYCNT").asText(null));          // 소비기한
                    p.setFormType(item.path("PRDT_SHAP_CD_NM").asText(null));       // 제품형태
                    p.setIntakeMethod(item.path("NTK_MTHD").asText(null));          // 섭취방법
                    p.setMainFunction(item.path("PRIMARY_FNCLTY").asText(null));    // 주된기능성
                    p.setRawMaterialsText(item.path("RAWMTRL_NM").asText(null));    // 원재료
                    p.setCautionText(item.path("IFTKN_ATNT_MATR_CN").asText(null)); // 섭취주의사항

                    p.setRawJson(item.toString()); // row 하나만 백업

                    rawProductRepository.save(p);
                    pageSaved++;
                }

                totalSaved += pageSaved;
                System.out.println("[품목제조신고] " + startIdx + "~" + endIdx +
                        " 구간에서 " + pageSaved + "건 저장 (누적 " + totalSaved + "건)");

                // 이번 페이지에서 덜 왔으면 = 마지막 페이지
                if (pageSaved < PRODUCT_PAGE_SIZE) {
                    System.out.println("[품목제조신고] 마지막 페이지 처리 완료 -> 종료");
                    break;
                }

                // 다음 페이지로
                startIdx += PRODUCT_PAGE_SIZE;

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("[품목제조신고] JSON 파싱 중 오류 -> 종료");
                break;
            }
        }

        System.out.println("[품목제조신고] 전체 저장 완료, 총 " + totalSaved + "건");
    }

    /**
     * 2) 식약처 "건강기능식품 개별인정형 원료 정보(I-0050)" API를 호출해
     *    JSON의 row 배열을 돌면서 raw_individual_ingredient 테이블에 여러 건 저장.
     */
    public void loadIndividualIngredientDataFromApi() {
    	
    	
        long count = rawIndividualIngredientRepository.count(); //이미 있으면 안가져오기
        if (count > 0) {
            System.out.println("[개별인정형] raw_individual_ingredient에 이미 " + count + "건 있음 -> API 호출 안 함");
            return;
        }
        
        
        
        RestTemplate restTemplate = new RestTemplate();

        int startIdx = 1;
        int totalSaved = 0;

        while (true) {
            int endIdx = startIdx + INGREDIENT_PAGE_SIZE - 1;

            // http://openapi.../key/I-0050/json/startIdx/endIdx
            String url = apiHost + "/" + apiKey + "/I-0050/json/" + startIdx + "/" + endIdx;

            System.out.println("[개별인정형] 요청: " + url);

            ResponseEntity<String> response =
                    restTemplate.getForEntity(url, String.class);

            String json = response.getBody();
            if (json == null || json.isEmpty()) {
                System.out.println("[개별인정형] 응답이 비어있음 -> 종료");
                break;
            }

            if (json.trim().startsWith("<")) {
                System.out.println("[개별인정형] JSON 아님 (스크립트/HTML 응답) -> 종료");
                break;
            }

            try {
                JsonNode root = objectMapper.readTree(json);
                JsonNode i0050 = root.path("I-0050");
                JsonNode rows = i0050.path("row");

                if (!rows.isArray() || rows.size() == 0) {
                    System.out.println("[개별인정형] 더 이상 row 없음 -> 종료");
                    break;
                }

                int pageSaved = 0;
                for (JsonNode item : rows) {
                    RawIndividualIngredient r = new RawIndividualIngredient();

                    r.setApprovalNo(item.path("HF_FNCLTY_MTRAL_RCOGN_NO").asText(null)); // 원료인정번호
                    r.setDailyMax(item.path("DAY_INTK_HIGHLIMIT").asText(null));         // 1일 섭취량 상한
                    r.setDailyMin(item.path("DAY_INTK_LOWLIMIT").asText(null));          // 1일 섭취량 하한
                    r.setUnit(item.path("WT_UNIT").asText(null));                        // 단위
                    r.setRawMaterialName(item.path("RAWMTRL_NM").asText(null));          // 원재료 명
                    r.setMainFunction(item.path("PRIMARY_FNCLTY").asText(null));         // 주된 기능성
                    r.setCautionText(item.path("IFTKN_ATNT_MATR_CN").asText(null));      // 주의 사항

                    r.setRawJson(item.toString());

                    rawIndividualIngredientRepository.save(r);
                    pageSaved++;
                }

                totalSaved += pageSaved;
                System.out.println("[개별인정형] " + startIdx + "~" + endIdx +
                        " 구간에서 " + pageSaved + "건 저장 (누적 " + totalSaved + "건)");

                if (pageSaved < INGREDIENT_PAGE_SIZE) {
                    System.out.println("[개별인정형] 마지막 페이지 처리 완료 -> 종료");
                    break;
                }

                startIdx += INGREDIENT_PAGE_SIZE;

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("[개별인정형] JSON 파싱 중 오류 -> 종료");
                break;
            }
        }

        System.out.println("[개별인정형] 전체 저장 완료, 총 " + totalSaved + "건");
    }
}
