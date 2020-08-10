package com.jjc.comm.common.sys;

import com.alibaba.fastjson.JSONObject;
import com.jjc.comm.common.util.MD5Util;


public class Result {
    private String rtnCode = "0";  //0成功
    private String msg;
    private String developMsg;
    private long ts = System.currentTimeMillis();
    private Object bizData;
    private String md5;
    private boolean needRefresh = true;

    public Result() {
    }

    public String getRtnCode() {
        return this.rtnCode;
    }

    public void setRtnCode(String rtnCode) {
        this.rtnCode = rtnCode;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDevelopMsg() {
        return this.developMsg;
    }

    public void setDevelopMsg(String developMsg) {
        this.developMsg = developMsg;
    }

    public long getTs() {
        return this.ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public Object getBizData() {
        return this.bizData;
    }

    public void setBizData(Object bizData) {
        if(bizData instanceof JSONObject) {
            JSONObject bizData_json = (JSONObject)bizData;
            Boolean needRefresh = bizData_json.getBoolean("needRefresh");
            if(needRefresh != null) {
                this.needRefresh = needRefresh.booleanValue();
                bizData_json.remove("needRefresh");
            }
        }

        this.bizData = bizData;
        if(bizData==null)
            this.md5 = MD5Util.GetMD5Code("");
        else
           this.md5 = MD5Util.GetMD5Code(bizData.toString());
    }

    public String getMd5() {
        return this.md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public boolean isNeedRefresh() {
        return this.needRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    @Override
    public String toString() {
        return "Result{" +
                "rtnCode='" + rtnCode + '\'' +
                ", msg='" + msg + '\'' +
                ", developMsg='" + developMsg + '\'' +
                ", ts=" + ts +
                ", bizData=" + bizData +
                ", md5='" + md5 + '\'' +
                ", needRefresh=" + needRefresh +
                '}';
    }
}
