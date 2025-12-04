package com.springboot.config;

import com.springboot.interceptor.AdminCheckInterceptor;
import com.springboot.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

    	// 로그인 체크
        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns(
                        "/members/me",      // 내 정보
                        "/supplements/**"   // 영양제 관련 페이지들
                );

        // 관리자 체크
        registry.addInterceptor(new AdminCheckInterceptor())
                .addPathPatterns("/admin/**");
    }
}
