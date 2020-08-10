package com.jjc.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置设置
 * @author huoquan
 * @date 2020/3/11.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    static final String ORIGINS[] = new String[] { "GET", "POST", "PUT","OPTIONS", "DELETE" };

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
//                .allowCredentials(true)
                .allowedMethods(ORIGINS)
                .allowedHeaders("*") //对应请求方法的Headers
                .exposedHeaders("Authorization","Content-Type")
                .allowCredentials(true) //对应请求的withCredentials 值
                .maxAge(3600);
    }
}
