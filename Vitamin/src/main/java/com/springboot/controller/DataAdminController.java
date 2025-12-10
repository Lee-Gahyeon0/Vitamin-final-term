package com.springboot.controller;

import com.springboot.service.FoodSafetyApiService;
import com.springboot.service.RawProductNormalizationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class DataAdminController {

    private final FoodSafetyApiService foodSafetyApiService;
    private final RawProductNormalizationService normalizationService;

    public DataAdminController(FoodSafetyApiService foodSafetyApiService,
                               RawProductNormalizationService normalizationService) {
        this.foodSafetyApiService = foodSafetyApiService;
        this.normalizationService = normalizationService;
    }

    @GetMapping("/load-products")
    public String loadProducts() {
        foodSafetyApiService.loadProductDataFromApi();
        return "raw_product 로딩 완료";
    }

    @GetMapping("/normalize")
    public String normalize() {
        normalizationService.normalizeAll();
        return "normalized_supplement 업데이트 완료";
    }
}
