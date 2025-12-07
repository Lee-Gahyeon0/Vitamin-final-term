package com.springboot.controller;

import com.springboot.domain.IntakeLog;
import com.springboot.domain.InteractionRule;
import com.springboot.domain.Member;
import com.springboot.domain.Supplement;
import com.springboot.dto.IntakeFormDto;
import com.springboot.service.IntakeLogService;
import com.springboot.service.InteractionService;
import com.springboot.service.SupplementService;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/intakes")
public class IntakeLogController {

    private final IntakeLogService intakeLogService;
    private final SupplementService supplementService;
    private final InteractionService interactionService;

    public IntakeLogController(IntakeLogService intakeLogService,
                               SupplementService supplementService,
                               InteractionService interactionService) {
        this.intakeLogService = intakeLogService;
        this.supplementService = supplementService;
        this.interactionService = interactionService;
    }

    /**
     * 1) 오늘 복용 기록 화면
     */
    @GetMapping("/today")
    public String today(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/members/login";
        }

        // 오늘 복용 기록
        List<IntakeLog> todayLogs = intakeLogService.getTodayLogs(loginMember.getId());
        model.addAttribute("todayLogs", todayLogs);

        // 내 영양제 목록
        List<Supplement> mySupplements = supplementService.getSupplements(loginMember.getId());
        model.addAttribute("supplements", mySupplements);
        
        model.addAttribute("hasSupplements", !mySupplements.isEmpty());
        
        model.addAttribute("intakeForm", new IntakeFormDto());
        
        
        // 오늘 복용 기준 상호작용 경고
        List<InteractionRule> interactions =
                interactionService.checkTodayInteractions(loginMember.getId());
        model.addAttribute("interactions", interactions);
        
        return "today";
    }

    /**
     * 2) 오늘 복용 기록 추가 처리
     */
    @PostMapping("/today")
    public String addTodayLog(@ModelAttribute("intakeForm") IntakeFormDto intakeForm,
                              HttpSession session,
                              Model model) {

    	Member loginMember = (Member) session.getAttribute("loginMember");
    	if (loginMember == null) {
    	    return "redirect:/members/login";
    	}

    	try {
    	    intakeLogService.addTodayLog(
    	            loginMember.getId(),
    	            intakeForm.getSupplementId(),
    	            intakeForm.getTimeSlot(),
    	            intakeForm.isTaken(),
    	            intakeForm.getMemo()
    	    );
    	    return "redirect:/intakes/today";

    	} catch (IllegalArgumentException e) {
    	    model.addAttribute("errorMessage", e.getMessage());
    	    
    	    List<IntakeLog> todayLogs = intakeLogService.getTodayLogs(loginMember.getId());
    	    model.addAttribute("todayLogs", todayLogs);

    	    List<Supplement> mySupplements = supplementService.getSupplements(loginMember.getId());
    	    model.addAttribute("supplements", mySupplements);
    	    model.addAttribute("hasSupplements", !mySupplements.isEmpty());

    	    List<InteractionRule> interactions =
    	            interactionService.checkTodayInteractions(loginMember.getId());
    	    model.addAttribute("interactions", interactions);

    	    // 사용자 입력 값 유지
    	    model.addAttribute("intakeForm", intakeForm);

            return "today";
        }
    }

    /**
     * 3) 복용 기록 히스토리
     */
    @GetMapping("/history")
    public String history(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/members/login";
        }

        List<IntakeLog> logs = intakeLogService.getHistory(loginMember.getId());
        model.addAttribute("logs", logs);

        return "history";   
    }
    

}
