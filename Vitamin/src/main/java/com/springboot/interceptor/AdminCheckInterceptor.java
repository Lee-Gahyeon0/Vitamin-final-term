package com.springboot.interceptor;

import com.springboot.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * [AdminCheckInterceptor]
 * - /admin/** 로 들어오는 요청에 대해
 *   1) 로그인 되어 있는지 확인
 *   2) 로그인 회원의 role 이 "ADMIN" 인지 확인
 * - 둘 중 하나라도 아니면 해당 요청을 막고, 다른 페이지로 리다이렉트함
 */
public class AdminCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
    	
    	// 기존 세션 가져오기 (없으면 null)
        HttpSession session = request.getSession(false);
        
        //세션 X ⇒ 로그인X
        if (session == null) {
            response.sendRedirect("/members/login");
            return false;
        }
        
        //회원정보 X ⇒ 로그인X
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            response.sendRedirect("/members/login");
            return false;
        }

        // 관리자X ⇒ 로그인X
        if (!"ADMIN".equals(loginMember.getRole())) {
            response.sendRedirect("/");
            return false;
        }

        return true;
    }
}
