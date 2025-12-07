package com.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.domain.RawProduct;
import com.springboot.domain.Supplement;
import com.springboot.service.SupplementService;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * [컨트롤러 계층]
 * - URL 요청을 받아서 Service를 호출하고, Thymeleaf 뷰에 데이터 전달
 */

@Controller
@RequestMapping("/supplements")
public class SupplementController {
	
	private SupplementService supplementService; // final 의존성 일단 빼둠
	
	public SupplementController(SupplementService supplementService) {
		this.supplementService = supplementService;
	}
	
	// -----------------------------
    // 1) 영양제 목록 페이지
    //    - 일단 memberId는 임시로 1번 고정 (로그인 기능 나중에)
    // -----
	@GetMapping("/list")
	public String list(Model model,
	                   @RequestParam(name = "memberId", defaultValue = "1") Long memberId) {
		
	    List<Supplement> supplements = supplementService.getSupplements(memberId);
	    model.addAttribute("supplements", supplements);
	    model.addAttribute("memberId", memberId); // 나중에 뷰에서 다시 넘겨줄 수 있게
		
	    return "list"; 
	}
	
	
	// -----------------------------
    // 2) 영양제 등록 폼 화면
    // -----------------------------
	@GetMapping("/new")
	public String showCreateForm(Model model,
	                             @RequestParam(name = "memberId", defaultValue = "1") Long memberId) {
		
        SupplementForm form = new SupplementForm();
        form.setMemberId(memberId); // 임시로 1번 회원 (또는 쿼리 파라미터)

        model.addAttribute("supplementForm", form);
        return "form"; 
    }
	
	
	// -----------------------------
    // 3) 영양제 등록 처리 (POST)
    // -----------------------------
	@PostMapping
	public String create(@ModelAttribute("supplementForm") SupplementForm form) {

	    supplementService.createSupplement(
	            form.getMemberId(),
	            form.getName(),
	            form.getBrand(),
	            form.getTags(),
	            form.getMemo()
	    );

	    // 등록 후 목록으로 리다이렉트 (memberId 유지)
	    return "redirect:/supplements/new?memberId=" + form.getMemberId();
	}
	 
	 
	// -----------------------------
    // 4) 식약처 제품 검색 화면 + 결과
    //    - /supplements/{id}/link?keyword=비타민
    // -----------------------------
	@GetMapping("/{id}/link")
	public String showLinkPage(@PathVariable("id") Long supplementId,
	                           @RequestParam(value = "keyword", required = false) String keyword,
	                           Model model) {

	    // 내 영양제 id를 뷰에 넘겨서, 나중에 어떤 영양제와 연결할지 유지
	    model.addAttribute("supplementId", supplementId);

	    if (keyword != null && !keyword.isBlank()) {
	        List<RawProduct> products = supplementService.searchRawProductsByName(keyword);
	        model.addAttribute("products", products);
	        model.addAttribute("keyword", keyword);
	    }

	    return "link-product";  // 검색 + 결과 보여주는 화면
	}

    // -----------------------------
    // 5) 선택한 식약처 제품과 내 영양제 매핑
    // -----------------------------
	@PostMapping("/{id}/link")
	public String linkRawProduct(@PathVariable("id") Long supplementId,
	                             @RequestParam("rawProductId") Long rawProductId,
	                             @RequestParam(name = "memberId", defaultValue = "1") Long memberId) {

	    supplementService.linkRawProduct(supplementId, rawProductId);

	    // 다시 내 영양제 목록으로 (memberId 유지)
	    return "redirect:/supplements/list?memberId=" + memberId;
	}

    // ============================
    // 폼 데이터 전달용 DTO (내부 클래스)
    // ============================
	@Getter
	@Setter
	public static class SupplementForm {
        // chap05에서 했던 "커맨드 객체" 느낌
	    private Long memberId;
	    private String name;
	    private String brand;
	    private String tags;
	    private String memo;
	}

}
