package com.jjc.api.commons.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取公用配置
 * @author huoquan
 * @date 2018/10/22.
 */
@Component
@ConfigurationProperties(prefix = "serialparams")
public class SerialPortConfig {

    private String serialPort;
    private int rate;
    private int maxNum;
    private int sendDelay;
    public int getMaxNum() {
        return maxNum;
    }
    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }
    public int getSendDelay() {
        return sendDelay;
    }
    public void setSendDelay(int sendDelay) {
        this.sendDelay = sendDelay;
    }
    public String getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(String serialPort) {
        this.serialPort = serialPort;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
