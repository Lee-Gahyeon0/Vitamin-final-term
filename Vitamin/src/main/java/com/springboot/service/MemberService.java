package com.springboot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.domain.Member;
import com.springboot.repository.MemberRepository;


/**
 * [서비스 계층 - MemberService]
 * - 컨트롤러와 레포지토리 사이에서
 *   "회원 가입/로그인" 비즈니스 로직 처리
 */

@Service
@Transactional
public class MemberService {
	
	private final MemberRepository memberRepository;
	
	 //생성자
	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	
    /**
     * 1) 회원 가입
     *  - 같은 이메일이 이미 있으면 예외 발생
     *  - 없으면 새 Member 저장
     */
	public Member register (String email, 
						    String password, 
						    String nickname) {
		
		System.out.println("=== [register] 호출됨 ===");
        System.out.println("email = " + email + ", password = " + password + ", nickname = " + nickname);
        
		// 이메일 중복 가입 확인
		Member existing = memberRepository.findByEmail(email);
		System.out.println("=== [register] existing = " + existing + " ===");
		if (existing != null) {
			throw new IllegalArgumentException("이미 가입된 이메일입니다:" + email);	
		}
		
		// 새 회원 엔티티 생성
		// 기본으로 USER로 가입됨
        Member m = new Member();
        m.setEmail(email);
        m.setPassword(password);
        m.setNickname(nickname); 
        
        Member saved = memberRepository.save(m);
        System.out.println("=== [register] 저장 완료, id = " + saved.getId() + " ===");

        return saved;
	}
	
	
	/**
     * 2) 로그인
     *  - 이메일로 회원 조회
     *  - 없으면 예외
     *  - 비밀번호 다르면 예외
     *  - 둘 다 맞으면 Member 리턴 (컨트롤러에서 세션에 넣기)
     */
	@Transactional(readOnly = true)
	public Member login(String email, 
						String password) {
		
		// 1. 이메일로 회원 찾기
        Member m = memberRepository.findByEmail(email);
        if (m == null) {
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }

        // 2. 비밀번호 비교
        if (!m.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 로그인 성공 → Member 반환
        return m;
	}
	
	
	 /**
     * 3) id로 회원 한 명 조회 (옵션)
     *  - 나중에 "내 정보" 같은 기능에서 사용 가능
     */
    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다. id=" + id));
    }

}
