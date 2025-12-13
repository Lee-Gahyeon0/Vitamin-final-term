package com.springboot.controller;

import com.springboot.service.FoodSafetyApiService;
import com.springboot.service.RawProductNormalizationService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class DataAdminController {

    private final FoodSafetyApiService foodSafetyApiService;
    private final RawProductNormalizationService normalizationService;

    public DataAdminController(FoodSafetyApiService foodSafetyApiService,
                               RawProductNormalizationService normalizationService) {
        this.foodSafetyApiService = foodSafetyApiService;
        this.normalizationService = normalizationService;
    }
    
    @GetMapping
    public String adminDashboard() {
        return "admindashboard";
    }

    @PostMapping("/load-products")
    public String loadProducts() {
        System.out.println("[ADMIN] load-products START");
        foodSafetyApiService.loadProductDataFromApi(); 
        System.out.println("[ADMIN] load-products DONE");
        return "redirect:/admin?done=load-products";
    }

    @PostMapping("/normalize")
    public String normalize() {
        System.out.println("[ADMIN] normalize START");
        normalizationService.normalizeAll(); 
        System.out.println("[ADMIN] normalize DONE");
        return "redirect:/admin?done=normalize";
    }
}