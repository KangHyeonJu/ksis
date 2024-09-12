package com.boot.ksis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
public class WebConfig implements WebMvcConfigurer {
   @Override
   public void addCorsMappings(CorsRegistry registry) {
       registry.addMapping("/**")
               .allowedOrigins("http://localhost:3000", "http://localhost:1212")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
               .allowedHeaders("*");
   }

    @Value("${uploadPath}")
    String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        CacheControl cacheControl = CacheControl.maxAge(Duration.ofDays(365));
        registry.addResourceHandler("/file/**")
                .addResourceLocations(uploadPath)
                .setCacheControl(cacheControl);
    }
}
