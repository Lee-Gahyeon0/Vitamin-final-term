package com.springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.service.FoodSafetyApiService;

@RestController
public class FoodSafetyApiController {

    private final FoodSafetyApiService foodSafetyApiService;

    public FoodSafetyApiController(FoodSafetyApiService foodSafetyApiService) {
        this.foodSafetyApiService = foodSafetyApiService;
    }
/**
 *  품목 DB로 옮김
 */

 
    // 품목제조신고 API -> raw_product 
    @GetMapping("/products")   
    public String loadProducts() {
        foodSafetyApiService.loadProductDataFromApi();
        return "products ok";
    }

    // 개별인정형 원료 API -> raw_individual_ingredient 
    @GetMapping("/ingredients") 
    public String loadIngredients() {
        foodSafetyApiService.loadIndividualIngredientDataFromApi();
        return "ingredients ok";
    }
}
