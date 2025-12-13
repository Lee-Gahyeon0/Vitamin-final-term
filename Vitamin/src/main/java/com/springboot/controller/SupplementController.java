package com.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.springboot.domain.Member;
import com.springboot.domain.NormalizedSupplement;
import com.springboot.domain.Supplement;
import com.springboot.dto.SupplementFormDto;
import com.springboot.service.SupplementService;

import jakarta.servlet.http.HttpSession;

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

    // 화면에서 선택할 태그 목록 (한글 버전)
    private static final List<String> TAG_OPTIONS = List.of(
            "철분", "칼슘", "비타민A", "비타민B", "비타민C", "비타민D", "멀티비타민",
            "오메가3", "유산균", "홍삼", "루테인", "식이섬유", "기타" 
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

        supplementService.createSupplement(memberId, form);

        // 등록 후 목록으로
        return "redirect:/supplements/list";
    }

    // -----------------------------
    // 4) 정규화 영양제 수동 연결 페이지
    //    - 검색 타입 (name/company/tag) + 키워드 받아서 검색
    // -----------------------------
    @GetMapping("/{id}/link")
    public String showLinkPage(@PathVariable("id") Long supplementId,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "type", required = false, defaultValue = "name") String type,
                               Model model) {

        Supplement supplement = supplementService.findById(supplementId);
        model.addAttribute("supplement", supplement);
        model.addAttribute("supplementId", supplementId);
        model.addAttribute("searchType", type);
        model.addAttribute("keyword", keyword == null ? "" : keyword);

        if (keyword != null && !keyword.isBlank()) {
            List<NormalizedSupplement> candidates =
                    supplementService.searchNormalizedSupplements(keyword, type);
            model.addAttribute("products", candidates);
        }

        return "link-product";  // 이 템플릿 안에서 products 목록 뿌려주고 선택
    }

    // -----------------------------
    // 5) 선택한 NormalizedSupplement와 내 영양제 매핑
    // -----------------------------
    @PostMapping("/{id}/link")
    public String linkNormalized(@PathVariable("id") Long supplementId,
                                 @RequestParam("normalizedId") Long normalizedId) {

        supplementService.manualLinkNormalizedSupplement(supplementId, normalizedId);
        return "redirect:/supplements/list";
    }

    // -----------------------------
    // 6) 삭제
    // -----------------------------
    @PostMapping("/{id}/delete")
    public String deleteSupplement(@PathVariable("id") Long id) {
        supplementService.deleteSupplement(id);
        return "redirect:/supplements/list";
    }

    // -----------------------------
    // 7) 수정 폼
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

        // "철분,비타민C" -> List<String>
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