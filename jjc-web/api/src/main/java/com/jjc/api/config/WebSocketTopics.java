package com.jjc.api.config;

public class WebSocketTopics {
    // 主题 。。。。
    public static final String GETRESPONSE= "getResponse";
    //通知指挥中心或者执勤台的客户端退出主题
    public static final String GETTOPICLOGIN ="getTopicLogin";
    public static final String TEST1= "TEST1";
    public static final String TEST2= "TEST2";


    // 主题前缀
    public static String getTopic(){
        return "/topic/";
    }
    // 主题前缀
    public static String getTopic(String topic){
        return "/topic/"+topic;
    }
}
