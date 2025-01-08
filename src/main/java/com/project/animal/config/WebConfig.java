package com.project.animal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "/uploads/**" URL 요청을 실제 파일 저장 디렉토리로 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:/Users/202016017/Desktop/gukbi/3rd_Project/gukbi_3rd_backend/uploads/");
    }
}