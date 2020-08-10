package com.jjc.comm.common.exception;

/**
 * Created by Administrator on 2017/9/12.
 */
public class BaseRuntimeException extends RuntimeException {
    protected RtnCode rtnCode;

    public BaseRuntimeException(String msg) {
        super(msg);
        this.rtnCode = RtnCode.ERROR;
    }

    public BaseRuntimeException(String msg, RtnCode rtnCode) {
        super(msg);
        this.rtnCode = RtnCode.ERROR;
        this.rtnCode = rtnCode;
    }

    public RtnCode getRtnCode() {
        return this.rtnCode;
    }

    public void setRtnCode(RtnCode rtnCode) {
        this.rtnCode = rtnCode;
    }
}
