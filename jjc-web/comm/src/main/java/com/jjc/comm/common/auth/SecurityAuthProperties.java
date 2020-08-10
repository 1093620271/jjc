package com.jjc.comm.common.auth;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author huoquan
 * @date 2018/11/8.
 */
//@Component
@ConfigurationProperties(prefix = "security.auth")
public class SecurityAuthProperties {
    private static Logger LOGGER = LoggerFactory.getLogger(SecurityAuthProperties.class);

    /**
     * 需要token认证的uri
     */
    private String uri = "/testData";

    private List<String> uriList;

    /**
     * debug模式
     */
    private boolean debug = false;

    /**
     * 是否只生成token
     */
    private boolean onlyGenerateToken = false;

    public SecurityAuthProperties() {
        uriList = initList(uri);
    }

    private List<String> initList(String str) {
        if (null != str && str.trim().length() > 0) {
            return Arrays.asList(str.split(","));
        } else {
            return new ArrayList<>();
        }
    }

    public List<String> getUriList() {
        return uriList;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        if (StringUtils.isNotBlank(uri)) {
            this.uri += uri;
        }
        LOGGER.info("需要安全认证的uri:{}", this.uri);
        this.uriList = initList(this.uri);
        this.uri = uri;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isOnlyGenerateToken() {
        return onlyGenerateToken;
    }

    public void setOnlyGenerateToken(boolean onlyGenerateToken) {
        this.onlyGenerateToken = onlyGenerateToken;
    }
}
