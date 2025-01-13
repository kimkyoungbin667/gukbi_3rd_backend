package com.project.animal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "/uploads/**" URL 요청을 실제 파일 저장 디렉토리로 매핑
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:C:/gukbi_3rd_backend1212/src/main/upload/"); // 실제 경로
    }
}