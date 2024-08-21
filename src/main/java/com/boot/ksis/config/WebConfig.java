package com.boot.ksis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:1212")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*");
    }

    String filePath = "C:\\Users\\codepc\\Documents\\GitHub\\ksis\\src\\main\\resources\\uploads\\40b9ea5f-8063-4f64-bb44-003078a598ba.png";
    String normalizedFilePath = filePath.replace("\\", "/");
// 이제 normalizedFilePath는 C:/Users/codepc/Documents/GitHub/ksis/src/main/resources/uploads/40b9ea5f-8063-4f64-bb44-003078a598ba.png가 됩니다.

}
