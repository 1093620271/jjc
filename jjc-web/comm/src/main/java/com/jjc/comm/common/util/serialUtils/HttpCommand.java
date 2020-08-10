package com.jjc.comm.common.util.serialUtils;

import com.alibaba.fastjson.JSON;

public class HttpCommand {
    public String url;
    public String data;
    public String type; // get 还是 post
    public String token; // token
    public boolean isPostMathod(){
        if("post".equalsIgnoreCase(type)){
            return true;
        }
        return false;
    }
    public String toJson(){
        return JSON.toJSONString(this);
    }

}
