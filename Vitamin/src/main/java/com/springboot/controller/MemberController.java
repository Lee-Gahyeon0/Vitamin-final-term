package com.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.domain.Member;
import com.springboot.service.MemberService;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/members")
public class MemberController {
	
	private final MemberService memberService;
	
	// 생성자 주입
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    
    
    /**
     * 1) 회원 가입 폼 화면
     */
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";  
    }

    
    
    /**
     * 2) 회원 가입 처리
     */
    @PostMapping("/register")
    public String register(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("nickname") String nickname,
            Model model
    ) {
        try {
            memberService.register(email, password, nickname);
         // 가입 성공 → 로그인 페이지로 이동
            return "redirect:/members/login";
        } catch (IllegalArgumentException e) {
        	// 서비스에서 던진 예외 메시지 화면에 보여주기
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    

    
    
    /**
     * 3) 로그인 폼 화면
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; 
    }

    
    
    /**
     * 4) 로그인 처리
     */
    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "redirectURL", required = false) String redirectURL,
            HttpSession session,
            Model model
    ) {
        try {
            Member loginMember = memberService.login(email, password);
            session.setAttribute("loginMember", loginMember);

            // 원래 가려던 페이지가 있으면 그쪽으로 복귀
            if (redirectURL != null && !redirectURL.isBlank()) {
                return "redirect:" + redirectURL;
            }

            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "login";
        }
    }

    

    /**
     * 5) 로그아웃
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();   // 세션 전체 삭제
        return "redirect:/";
    }

    
    /**
     * 6) 내 정보 보기 (옵션)
     */
    @GetMapping("/me")
    public String myInfo(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            // 로그인 안 되어 있으면 로그인 페이지로
            return "redirect:/login";
        }

        // DB에서 다시 조회해서 넘기기
        Member member = memberService.findById(loginMember.getId());

        model.addAttribute("member", member);
        return "/myInfo";   // templates/members/myInfo.html
    }

}
