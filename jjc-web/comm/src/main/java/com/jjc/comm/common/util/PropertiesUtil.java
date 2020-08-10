package com.jjc.comm.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by weicheng on 2017/10/26.
 */
public class PropertiesUtil {
    private InputStream is;
    private static Properties properties;

    public PropertiesUtil() {
        is = this.getClass().getResourceAsStream("/const.properties");// 将配置文件读入输入流中
        properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            System.out.println("配置文件不存在..");
            e.printStackTrace();
        } finally {

            if (null != is) {

                try {
                    is.close();
                } catch (IOException e) {
                    System.out.println("关闭流失败..");
                    e.printStackTrace();
                }
            }

        }

    }

    public PropertiesUtil(String path) {
        is = this.getClass().getResourceAsStream(path);// 将配置文件读入输入流中
        properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            System.out.println("配置文件不存在..");
            e.printStackTrace();
        } finally {

            if (null != is) {

                try {
                    is.close();
                } catch (IOException e) {
                    System.out.println("关闭流失败..");
                    e.printStackTrace();
                }
            }

        }

    }

    public static String getValue(String key){
        String value="";
        try {
            new PropertiesUtil();
            value=properties.getProperty(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }

    public static String getTemplateValue(String key){
        String value="";
        try {
            new PropertiesUtil("/template/template.properties");
            value=properties.getProperty(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }

    public static String getIp() {// 获取ftp服务器的ip地址
        return getValue("ftpIp");

    }

    public static String getPort() {// 获取ftp服务器的端口
        return getValue("ftpPort");

    }

    public static String getUser() {// 获取ftp登录用户名
        return getValue("ftpUser");

    }

    public static String getPwd() {// 获取ftp服务器的登录密码
        return getValue("ftpPwd");

    }

}
