package com.springboot.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        // 로그인 안됨 → 팝업 띄우고 로그인으로 + 원래 URL 저장(쿼리스트링까지)
        if (session == null || session.getAttribute("loginMember") == null) {

            String uri = request.getRequestURI();        // /supplements/3/edit
            String qs = request.getQueryString();        // memberId=1  (없을 수도 있음)
            String fullUrl = (qs == null) ? uri : (uri + "?" + qs);

            String redirectURL = URLEncoder.encode(fullUrl, StandardCharsets.UTF_8);

            response.sendRedirect("/members/login?needLogin=true&redirectURL=" + redirectURL);
            return false;
        }

        return true;
    }
}
