package com.jjc.api.commons.entity;

/**
 * api接口通用返回接口
 * @author huoquan
 * @date 2017/12/19.
 */
public class ResponseModel {

    //状态说明：true返回成功，false未知错误
    private Boolean success=true;
    //数据
    private Object data="";
    //消息说明
    private String errorMessge="";

    private int code = 0;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getErrorMessge() {
        return errorMessge;
    }

    public void setErrorMessge(String errorMessge) {
        this.errorMessge = errorMessge;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
