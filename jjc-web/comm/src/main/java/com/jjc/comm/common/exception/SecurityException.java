package com.jjc.comm.common.exception;

/**
 * Created by Administrator on 2017/9/12.
 */
public class SecurityException extends BaseRuntimeException {
    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, RtnCode code) {
        super(message, code);
    }
}
