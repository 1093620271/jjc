package com.jjc.api.commons.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取公用配置
 * @author huoquan
 * @date 2018/10/22.
 */
@Component
@ConfigurationProperties(prefix = "common")
public class CommonConfig {

    private String superUserName;
    private String superPwd;
    private String uploadPath;
    private String uploadHttpPath;
    private String captcha;
    private String syncFileToFtp;
    private String ftpPath;
    private String ftpHttpPath;
    private String cacheType;
    private String ordinaryUserName;
    private Integer timeInterval;
    private String enable;
    private Integer obtainCardTime;
    private String ordinaryPwd;

    public String getOrdinaryPwd() {
        return ordinaryPwd;
    }

    public void setOrdinaryPwd(String ordinaryPwd) {
        this.ordinaryPwd = ordinaryPwd;
    }

    public Integer getObtainCardTime() {
        return obtainCardTime;
    }

    public void setObtainCardTime(Integer obtainCardTime) {
        this.obtainCardTime = obtainCardTime;
    }

    public Integer getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(Integer timeInterval) {
        this.timeInterval = timeInterval;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getOrdinaryUserName() {
        return ordinaryUserName;
    }

    public void setOrdinaryUserName(String ordinaryUserName) {
        this.ordinaryUserName = ordinaryUserName;
    }

    public String getSuperUserName() {
        return superUserName;
    }

    public void setSuperUserName(String superUserName) {
        this.superUserName = superUserName;
    }

    public String getSuperPwd() {
        return superPwd;
    }

    public void setSuperPwd(String superPwd) {
        this.superPwd = superPwd;
    }


    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getUploadHttpPath() {
        return uploadHttpPath;
    }

    public void setUploadHttpPath(String uploadHttpPath) {
        this.uploadHttpPath = uploadHttpPath;
    }


    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getSyncFileToFtp() {
        return syncFileToFtp;
    }

    public void setSyncFileToFtp(String syncFileToFtp) {
        this.syncFileToFtp = syncFileToFtp;
    }

    public String getFtpPath() {
        return ftpPath;
    }

    public void setFtpPath(String ftpPath) {
        this.ftpPath = ftpPath;
    }

    public String getFtpHttpPath() {
        return ftpHttpPath;
    }

    public void setFtpHttpPath(String ftpHttpPath) {
        this.ftpHttpPath = ftpHttpPath;
    }

    public String getCacheType() {
        return cacheType;
    }

    public void setCacheType(String cacheType) {
        this.cacheType = cacheType;
    }
}
