package com.springboot.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> factory.addConnectorCustomizers(connector -> {
            // maxFileCount: 업로드 가능한 파트(파일+텍스트필드)의 최대 개수
            connector.setProperty("maxParameterCount", "1000");
            connector.setProperty("maxPostSize", "20971520"); // 20MB
            connector.setProperty("maxFileCount", "200"); 
        });
    }
}