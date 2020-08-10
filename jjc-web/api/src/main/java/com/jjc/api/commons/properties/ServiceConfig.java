package com.jjc.api.commons.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取公用配置
 * @author huoquan
 * @date 2018/10/22.
 */
@Component
@ConfigurationProperties(prefix = "server")
public class ServiceConfig {
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
