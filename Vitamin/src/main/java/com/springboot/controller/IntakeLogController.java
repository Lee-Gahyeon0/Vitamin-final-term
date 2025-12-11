package com.springboot.controller;

import com.springboot.domain.IntakeLog;
import com.springboot.domain.InteractionRule;
import com.springboot.domain.Member;
import com.springboot.domain.Supplement;
import com.springboot.dto.IntakeFormDto;
import com.springboot.repository.IntakeLogRepository;
import com.springboot.service.IntakeLogService;
import com.springboot.service.InteractionService;
import com.springboot.service.SupplementService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/intakes")
public class IntakeLogController {

    private final IntakeLogService intakeLogService;
    private final SupplementService supplementService;
    private final InteractionService interactionService;
    private final IntakeLogRepository intakeLogRepository;

    public IntakeLogController(IntakeLogService intakeLogService,
                               SupplementService supplementService,
                               InteractionService interactionService,
                               IntakeLogRepository intakeLogRepository) {
        this.intakeLogService = intakeLogService;
        this.supplementService = supplementService;
        this.interactionService = interactionService;
        this.intakeLogRepository = intakeLogRepository;
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

        Long memberId = loginMember.getId();

        // 오늘 복용 기록
        List<IntakeLog> todayLogs = intakeLogService.getTodayLogs(memberId);
        model.addAttribute("todayLogs", todayLogs);

        // 지금까지 전체 복용 기록
        List<IntakeLog> allLogs = intakeLogService.getHistory(memberId);
        model.addAttribute("allLogs", allLogs);

        // 요약 통계 
        int totalLogs = allLogs.size();
        int takenCount = 0;

        for (IntakeLog log : allLogs) {
            if (log.isTaken()) {
                takenCount++;
            }
        }

        double takenRate = 0.0;
        if (totalLogs > 0) {
            takenRate = (takenCount * 100.0) / totalLogs;
        }

        model.addAttribute("totalLogs", totalLogs);
        model.addAttribute("takenCount", takenCount);
        model.addAttribute("takenRate", takenRate);

        // 내 영양제 목록
        List<Supplement> mySupplements = supplementService.getSupplements(memberId);
        model.addAttribute("supplements", mySupplements);
        model.addAttribute("hasSupplements", !mySupplements.isEmpty());

        // 입력 폼
        model.addAttribute("intakeForm", new IntakeFormDto());

        // 오늘 복용 기준 상호작용 경고
        List<InteractionRule> interactions =
                interactionService.checkTodayInteractions(memberId);
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
     * 3) 복용 기록 히스토리 화면 (원래 있던 거 그대로)
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

    /**
     * 4) 복용 기록 CSV로 내보내기
     */
    @GetMapping("/export")
    public void exportLogsAsCsv(HttpServletResponse response,
                                HttpSession session) throws IOException {

        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Long memberId = loginMember.getId();

        // 파일 이름 설정
        String fileName = "intake_logs_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + fileName + "\"");

        // 여기서 서비스 메서드 이름은 네가 실제로 만든 걸로 맞춰야 함
        List<IntakeLog> logs = intakeLogService.getAllLogsForMember(memberId);

        PrintWriter writer = response.getWriter();

        // 헤더
        writer.println("날짜,복용시간대,영양제이름,복용여부,메모");


        for (IntakeLog log : logs) {
            String date = log.getDate() != null ? log.getDate().toString() : "";
            String timeSlot = log.getTimeSlot(); // String
            String supplementName = (log.getSupplement() != null)
                    ? log.getSupplement().getName()
                    : "";
            String takenStr = log.isTaken() ? "Y" : "N";
            String memo = log.getMemo() != null ? log.getMemo().replace(",", " ") : "";

            writer.printf("%s,%s,%s,%s,%s%n",
                    date, timeSlot, supplementName, takenStr, memo);
        }

        writer.flush();
    }


}
