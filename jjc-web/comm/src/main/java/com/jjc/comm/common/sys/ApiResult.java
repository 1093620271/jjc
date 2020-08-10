package com.jjc.comm.common.sys;

/**
 * api接口通用返回接口
 * @author huoquan
 * @date 2017/12/19.
 */
public class ApiResult {

    //状态说明：0返回成功，1未知错误
    private int code=0;
    //消息说明
    private String msg="操作成功";
    //平台返回数据中 最大的一条时间戳
    private long maxSeq;
    //平台返回满足条件总共有多少条数据，主要用来分页请求
    private long total;
    //可为空，用于传递数据对象或集合
    private Object obj;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getMaxSeq() {
        return maxSeq;
    }

    public void setMaxSeq(long maxSeq) {
        this.maxSeq = maxSeq;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
