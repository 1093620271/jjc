package com.jjc.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 解决Tomcat 报 The valid characters are defined in RFC 7230 and RFC 3986 的问题
 * @author huoquan
 * @date 2019/3/4.
 */

@Configuration
public class RfcConfig {
    @Bean
    public Integer setRfc()
    {
        // 指定jre系统属性，允许特殊符号， 如{} 做入参，其他符号按需添加。见 tomcat的HttpParser源码。
        System.setProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow", "|{}");
        return 0;
    }
}
