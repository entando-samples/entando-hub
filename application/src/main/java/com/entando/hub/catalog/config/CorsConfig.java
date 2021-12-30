package com.entando.hub.catalog.config;

import org.dom4j.xpath.XPathPattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Value("${HUB_GROUP_DETAIL_BASE_URL}")
    private String allowedOrigin;

//    @Bean
//    public WebMvcConfigurer getCorsConfigurer(){
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins(allowedOrigin)
//                        .allowedMethods("*")
//                        .allowedHeaders("*");
//            }
//        };
//    }
}

