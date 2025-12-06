package com.springboot.service;

import com.springboot.domain.IntakeLog;
import com.springboot.domain.Member;
import com.springboot.domain.Supplement;
import com.springboot.repository.IntakeLogRepository;
import com.springboot.repository.MemberRepository;
import com.springboot.repository.SupplementRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class IntakeLogService {

    private final IntakeLogRepository intakeLogRepository;
    private final MemberRepository memberRepository;
    private final SupplementRepository supplementRepository;

    public IntakeLogService(IntakeLogRepository intakeLogRepository,
                            MemberRepository memberRepository,
                            SupplementRepository supplementRepository) {
        this.intakeLogRepository = intakeLogRepository;
        this.memberRepository = memberRepository;
        this.supplementRepository = supplementRepository;
    }

    /**
     * 오늘 날짜 기준 복용 기록 조회
     */
    @Transactional(readOnly = true)
    public List<IntakeLog> getTodayLogs(Long memberId) {
        LocalDate today = LocalDate.now();
        return intakeLogRepository.findByMemberIdAndDate(memberId, today);
    }

    /**
     * 전체 복용 기록(최근 날짜부터) 조회
     */
    @Transactional(readOnly = true)
    public List<IntakeLog> getHistory(Long memberId) {
        return intakeLogRepository.findByMemberIdOrderByDateDesc(memberId);
    }

    /**
     * 오늘 복용 기록 추가
     */
    public IntakeLog addTodayLog(Long memberId,
                                 Long supplementId,
                                 String timeSlot,
                                 boolean taken,
                                 String memo) {

        LocalDate today = LocalDate.now();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다. id=" + memberId));

        Supplement supplement = supplementRepository.findById(supplementId)
                .orElseThrow(() -> new IllegalArgumentException("영양제가 존재하지 않습니다. id=" + supplementId));

        IntakeLog log = new IntakeLog();
        log.setMember(member);
        log.setSupplement(supplement);
        log.setDate(today);
        log.setTimeSlot(timeSlot);  // 엔티티에서 String/Enum 중 뭐 쓰는지에 맞게
        log.setTaken(taken);
        log.setMemo(memo);

        return intakeLogRepository.save(log);
    }
}
