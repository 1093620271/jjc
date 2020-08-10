package com.jjc.api.config;

import com.alibaba.fastjson.serializer.SerializerFeature;

import com.jjc.api.commons.convert.MessageConverter;
import com.jjc.api.commons.interceptor.LoginInterceptor;
import com.jjc.comm.common.interceptor.LogbackInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * mvc配置
 * @author huoquan
 * @date 2018/10/22.
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        //super.configurePathMatch(configurer);
        //访问路径不区分大小写
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
        configurer.setPathMatcher(matcher);
    }

    /**
     * 消息内容转换配置
     * 配置fastJson返回json转换
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //调用父类的配置
        super.configureMessageConverters(converters);
        //将fastjson添加到视图消息转换器列表内
        converters.add(converter());
    }

    @Bean
    public MessageConverter converter() {
        //创建fastJson消息转换器
        MessageConverter fastConverter = new MessageConverter();
        //创建配置类
        List<MediaType> supportedMediaTypes=new ArrayList<MediaType>();
        supportedMediaTypes.add(MediaType.TEXT_HTML);
        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(supportedMediaTypes);
        //fastConverter.setFeatures(SerializerFeature.WriteMapNullValue,SerializerFeature.QuoteFieldNames,SerializerFeature.WriteNullStringAsEmpty);
        fastConverter.setFeatures(SerializerFeature.WriteMapNullValue,SerializerFeature.QuoteFieldNames);
        return fastConverter;
    }

    /**
     * 拦截器实现
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //=================开始==logback日志拦截器==============================
        InterceptorRegistration logbackRegistration=registry.addInterceptor(getLogbackInterceptor());
        //需要拦截的url
        logbackRegistration.addPathPatterns("/**");
        //不需要拦截的url
        logbackRegistration.excludePathPatterns("/static/**");
        //=================结束==logback日志拦截器==============================

        //=================开始==权限拦截器==================================
        InterceptorRegistration registration=registry.addInterceptor(getLoginInterceptor());
        //需要拦截的url
        List<String> pathPatterns=new ArrayList<String>();
        pathPatterns.add("/**");
        registration.addPathPatterns(pathPatterns);
        //不需要拦截的url
        List<String> excludePathPatterns=new ArrayList<String>();
        excludePathPatterns.add("/");
        excludePathPatterns.add("/static/**");
        excludePathPatterns.add("/api-docs/**");
        excludePathPatterns.add("/tenant/updateTenant.do");
        registration.excludePathPatterns(excludePathPatterns);
        //=================结束==权限拦截器==================================
    }

    @Bean   //把拦截器注入为bean
    public HandlerInterceptor getLoginInterceptor(){
        return new LoginInterceptor();
    }

    @Bean   //把日志拦截器注入为bean
    public HandlerInterceptor getLogbackInterceptor(){
        return new LogbackInterceptor();
    }

}
