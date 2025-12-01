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
    private final RawProductRepository  rawProductRepository;
    
    public SupplementService(SupplementRepository supplementRepository,
    						 MemberRepository memberRepository,
    						 RawProductRepository  rawProductRepository) {
    	this.supplementRepository = supplementRepository;
    	this.memberRepository = memberRepository;
    	this.rawProductRepository = rawProductRepository;
    }



/**
 * 1) 내 영양제 등록
 * - memberId: 어느 회원이 등록하는지
 * - name, brand, tags, memo: 폼에서 입력한 값
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
	
	//3. 저장
	return supplementRepository.save(s);
}
	


	/**
	 * 2) 특정 회원의 영양제 목록 가져오기
	 */
	@Transactional(readOnly = true)
	public List<Supplement> getSupplements(Long memeberId){
		return supplementRepository.findByMemberId(memeberId);
	}
	
	
	/**
     * 3) 식약처 RAW 제품에서 제품명으로 검색
     *    - 나중에 "검색 후, 하나 선택해서 내 영양제와 매핑"에 사용
     */
	@Transactional(readOnly = true)
	public List<RawProduct> serchRawProductsByName(String keyword){
		return rawProductRepository.findByProductNameContaining(keyword);
	}
	
	/**
     * 4) 내 영양제와 식약처 RAW 제품 매핑하기
     *    - supplementId: 내가 등록한 영양제
     *    - rawProductId: 식약처에서 가져온 제품 id (raw_product.id)
     */
	
	public  Supplement linkRawProduct(Long supplementId, Long rawProductId) {
		Supplement supplement = supplementRepository.findById(supplementId)
				.orElseThrow(() -> new IllegalArgumentException("영양제를 찾을 수 없습니다. id=" + supplementId));
		
		RawProduct rawProduct = rawProductRepository.findById(rawProductId)
                .orElseThrow(() -> new IllegalArgumentException("식약처 제품을 찾을 수 없습니다. id=" + rawProductId));
		
		// 연결
		supplement.setRawProduct(rawProduct);
		
		// save 는 @Transactional 안에서 영속 상태라 변경 자동 반영됨
        return supplement;
	}

}

