package com.jjc.api.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 图像验证码
 * 配置说明：http://www.ibloger.net/article/135.html
 * @author tony
 * @date 2019/1/24.
 */
@Component
public class CaptchaConfig {
    @Bean
    public DefaultKaptcha getDefaultKaptcha(){
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "yes"); //图片边框，合法值：yes , no,默认yes
        properties.setProperty("kaptcha.border.color", "white"); //边框颜色,默认	black
//        properties.setProperty("kaptcha.textproducer.font.color", "blue"); //字体颜色,默认black
        properties.setProperty("kaptcha.image.width", "120"); //图片宽,默认200
        properties.setProperty("kaptcha.image.height", "40"); //	图片高,默认50
        properties.setProperty("kaptcha.textproducer.font.size", "30"); //字体大小,默认40px.
        properties.setProperty("kaptcha.session.key", "code"); //session key,默认KAPTCHA_SESSION_KEY
        properties.setProperty("kaptcha.textproducer.char.space", "6"); //	文字间隔,默认2
        properties.setProperty("kaptcha.textproducer.char.length", "4");  //验证码长度,默认5
        properties.setProperty("kaptcha.textproducer.font.names", "彩云,宋体,楷体,微软雅黑"); //字体,默认Arial, Courier
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
