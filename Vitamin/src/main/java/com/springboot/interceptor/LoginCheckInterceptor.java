package com.springboot.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 기존 세션 있으면 가져오고, 없으면 null
        HttpSession session = request.getSession(false);

        // 세션 없거나, 로그인 정보 없으면 → 로그인 페이지로
        if (session == null || session.getAttribute("loginMember") == null) {
            response.sendRedirect("/members/login");
            return false;
        }

        // 로그인 되어 있으면 통과
        return true;
    }
}

