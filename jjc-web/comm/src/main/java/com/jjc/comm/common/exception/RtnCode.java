package com.jjc.comm.common.exception;

/**
 * Created by Administrator on 2017/9/12.
 */
public enum RtnCode {
    OK(0, "OK"),
    ERROR(1, "未知错误"),
    NO_LOIN(100, "用户未登陆"),
    TOKEN_EXPIRE(101, "签名超时"),
    TOKEN_WRONG(102, "签名错误");

    private final int value;
    private final String reasonPhrase;

    private RtnCode(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public static RtnCode valueOf(int statusCode) {
        RtnCode[] var1 = values();
        int length = var1.length;

        for(int i = 0; i < length; ++i) {
            RtnCode status = var1[i];
            if(status.value == statusCode) {
                return status;
            }
        }

        throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
    }

    public int value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public String toString() {
        return Integer.toString(this.value);
    }
}
