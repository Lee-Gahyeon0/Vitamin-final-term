package com.springboot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.domain.Member;
import com.springboot.domain.RawProduct;
import com.springboot.domain.Supplement;
import com.springboot.repository.MemberRepository;
import com.springboot.repository.RawProductRepository;
import com.springboot.repository.SupplementRepository;

@Service
@Transactional
public class SupplementService {

    private final SupplementRepository supplementRepository;
    private final MemberRepository memberRepository;
    private final RawProductRepository rawProductRepository;

    public SupplementService(SupplementRepository supplementRepository,
                             MemberRepository memberRepository,
                             RawProductRepository rawProductRepository) {
        this.supplementRepository = supplementRepository;
        this.memberRepository = memberRepository;
        this.rawProductRepository = rawProductRepository;
    }

    
    
    /**
     * 1) 내 영양제 등록
     */
    public Supplement createSupplement(Long memberId,
                                       String name,
                                       String brand,
                                       String tags,
                                       String memo) {

        // 1. 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다. id=" + memberId));

        // 2. supplement 엔티티 생성 및 값 세팅
        Supplement s = new Supplement();
        s.setMember(member);
        s.setName(name);
        s.setBrand(brand);
        s.setTags(tags);
        s.setMemo(memo);

        // 3. 저장
        return supplementRepository.save(s);
    }

    
    
    /**
     * 2) 특정 회원의 영양제 목록 가져오기
     */
    @Transactional(readOnly = true)
    public List<Supplement> getSupplements(Long memberId) {
        return supplementRepository.findByMemberId(memberId);
    }

    
    
    /**
     * 3) 영양제 한 개 조회 (수정 폼, 상세보기 등에서 사용)
     */
    @Transactional(readOnly = true)
    public Supplement findById(Long id) {
        return supplementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("영양제를 찾을 수 없습니다. id=" + id));
    }

    
    
    /**
     * 4) 영양제 수정
     */
    @Transactional
    public void updateSupplement(Long id,
                                 String name,
                                 String brand,
                                 String tags,
                                 String memo) {

        Supplement supplement = findById(id);   

        supplement.setName(name);
        supplement.setBrand(brand);
        supplement.setTags(tags);
        supplement.setMemo(memo);
    }
   
   
    
    
    /**
     * 5) 식약처 RAW 제품에서 제품명으로 검색
     */
    @Transactional(readOnly = true)
    public List<RawProduct> searchRawProductsByName(String keyword) {
        return rawProductRepository.findByProductNameContaining(keyword);
    }

    
    
    /**
     * 6) 내 영양제와 식약처 RAW 제품 매핑하기
     */
    public Supplement linkRawProduct(Long supplementId, Long rawProductId) {

        // (1) 내 영양제 조회
        Supplement supplement = supplementRepository.findById(supplementId)
                .orElseThrow(() -> new IllegalArgumentException("영양제를 찾을 수 없습니다. id=" + supplementId));

        // (2) 식약처 제품 조회
        RawProduct rawProduct = rawProductRepository.findById(rawProductId)
                .orElseThrow(() -> new IllegalArgumentException("식약처 제품을 찾을 수 없습니다. id=" + rawProductId));

        // 연결
        supplement.setRawProduct(rawProduct);

        return supplement;
    }

    
    
    /**
     * 7) 삭제
     */
    public void deleteSupplement(Long id) {
        supplementRepository.deleteById(id);
    }
}
