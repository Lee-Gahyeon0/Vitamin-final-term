package com.springboot.config;

import com.springboot.interceptor.AdminCheckInterceptor;
import com.springboot.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // application.properties 의 file.upload-dir 읽어오기
    @Value("${file.upload-dir}")
    private String uploadDir; // 예: C:/vitamin-uploads

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 로그인 체크
        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns(
                        "/members/me",      // 내 정보
                        "/supplements/**",   // 영양제 관련 페이지들
                        "/intakes/**"
                );

        // 관리자 체크
        registry.addInterceptor(new AdminCheckInterceptor())
                .addPathPatterns("/admin/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 윈도우 경로를 file URL 형태로 변환
        String normalizedPath = uploadDir.replace("\\", "/");
        if (!normalizedPath.endsWith("/")) {
            normalizedPath += "/";
        }

        // http://localhost:8080/vitamin-images/파일명
        //   -> C:/vitamin-uploads/파일명
        registry.addResourceHandler("/vitamin-images/**")
                .addResourceLocations("file:///" + normalizedPath);
    }
}
