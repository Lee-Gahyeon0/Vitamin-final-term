package com.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.domain.Member;
import com.springboot.domain.RawProduct;
import com.springboot.domain.Supplement;
import com.springboot.dto.SupplementFormDto;
import com.springboot.service.SupplementService;

import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/**
 * [컨트롤러 계층]
 * - URL 요청을 받아서 Service를 호출하고, Thymeleaf 뷰에 데이터 전달
 */

@Controller
@RequestMapping("/supplements")
public class SupplementController {

    private final SupplementService supplementService;
    
    private static final List<String> TAG_OPTIONS = List.of(
            "철분",
            "칼슘",
            "비타민C",
            "비타민B",
            "오메가3",
            "유산균",
            "홍삼",
            "루테인",
            "멀티비타민"
    );

    public SupplementController(SupplementService supplementService) {
        this.supplementService = supplementService;
    }

    // -----------------------------
    // 1) 영양제 목록 페이지
    // -----------------------------
    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/members/login";
        }

        Long memberId = loginMember.getId();

        List<Supplement> supplements = supplementService.getSupplements(memberId);
        model.addAttribute("supplements", supplements);
        model.addAttribute("memberId", memberId);  

        return "list";
    }

    // -----------------------------
    // 2) 영양제 등록 폼 화면
    // -----------------------------
    @GetMapping("/new")
    public String showCreateForm(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/members/login";
        }

        SupplementFormDto form = new SupplementFormDto();
        form.setMemberId(loginMember.getId());  
        
        model.addAttribute("supplementForm", form);
        model.addAttribute("tagOptions", TAG_OPTIONS);

        return "form";
    }

    // -----------------------------
    // 3) 영양제 등록 처리 (POST)
    // -----------------------------
    @PostMapping
    public String create(@ModelAttribute("supplementForm") SupplementFormDto form,
                         HttpSession session,
                         Model model) {

        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/members/login";
        }
        Long memberId = loginMember.getId();  

        boolean hasError = false;

        // 이름 검사
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            model.addAttribute("nameError", "제품명을 입력해 주세요.");
            hasError = true;
        }

        // 브랜드 검사
        if (form.getBrand() == null || form.getBrand().trim().isEmpty()) {
            model.addAttribute("brandError", "브랜드를 입력해 주세요.");
            hasError = true;
        }

        // 태그 검사: tagCodes 기준
        if (form.getTagCodes() == null || form.getTagCodes().isEmpty()) {
            model.addAttribute("tagsError", "최소 한 개 이상의 태그를 선택해 주세요.");
            hasError = true;
        }

        if (hasError) {
            model.addAttribute("supplementForm", form);
            model.addAttribute("tagOptions", TAG_OPTIONS);
            return "form";
        }

        supplementService.createSupplement(memberId, form);
        
        // 등록 후 목록으로 (memberId 파라미터 필요 없음)
        return "redirect:/supplements/list";
    }

    // -----------------------------
    // 4) 식약처 제품 검색 화면 + 결과
    // -----------------------------
    @GetMapping("/{id}/link")
    public String showLinkPage(@PathVariable("id") Long supplementId,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               Model model) {

        model.addAttribute("supplementId", supplementId);

        if (keyword != null && !keyword.isBlank()) {
            List<RawProduct> products = supplementService.searchRawProductsByName(keyword);
            model.addAttribute("products", products);
            model.addAttribute("keyword", keyword);
        }

        return "link-product";
    }

    // -----------------------------
    // 5) 선택한 식약처 제품과 내 영양제 매핑
    // -----------------------------
    @PostMapping("/{id}/link")
    public String linkRawProduct(@PathVariable("id") Long supplementId,
                                 @RequestParam("rawProductId") Long rawProductId) {

        supplementService.linkRawProduct(supplementId, rawProductId);

        return "redirect:/supplements/list";
    }

    // -----------------------------
    // 6) 리스트 삭제
    // -----------------------------
    @PostMapping("/{id}/delete")
    public String deleteSupplement(@PathVariable("id") Long id) {
        supplementService.deleteSupplement(id);
        return "redirect:/supplements/list";
    }

    // -----------------------------
    // 7) 리스트 수정 폼
    // -----------------------------
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {

        Supplement supplement = supplementService.findById(id);

        SupplementFormDto form = new SupplementFormDto();
        form.setId(supplement.getId());
        form.setMemberId(supplement.getMember().getId());
        form.setName(supplement.getName());
        form.setBrand(supplement.getBrand());
        form.setMemo(supplement.getMemo());

        List<String> tagCodes = new ArrayList<>();
        if (supplement.getTags() != null && !supplement.getTags().isBlank()) {
            tagCodes = Arrays.asList(supplement.getTags().split(","));
        }
        form.setTagCodes(tagCodes);

        model.addAttribute("supplementForm", form);
        model.addAttribute("tagOptions", TAG_OPTIONS);

        return "form";
    }

    
    
    // -----------------------------
    // 8) 수정 처리
    // -----------------------------
    @PostMapping("/{id}/edit")
    public String updateSupplement(@PathVariable("id") Long id,
                                   @ModelAttribute("supplementForm") SupplementFormDto form,
                                   Model model) {

        boolean hasError = false;

        if (form.getName() == null || form.getName().trim().isEmpty()) {
            model.addAttribute("nameError", "제품명을 입력해 주세요.");
            hasError = true;
        }
        if (form.getBrand() == null || form.getBrand().trim().isEmpty()) {
            model.addAttribute("brandError", "브랜드를 입력해 주세요.");
            hasError = true;
        }
        if (form.getTagCodes() == null || form.getTagCodes().isEmpty()) {
            model.addAttribute("tagsError", "최소 한 개 이상의 태그를 선택해 주세요.");
            hasError = true;
        }

        if (hasError) {
            model.addAttribute("supplementForm", form);
            model.addAttribute("tagOptions", TAG_OPTIONS);
            return "form";
        }

        supplementService.updateSupplement(id, form);

        return "redirect:/supplements/list";
    }
}