package com.jjc.comm.common.exception;

/**
 * Created by Administrator on 2017/9/12.
 */
public class ApiControllerException extends BaseRuntimeException {
    public ApiControllerException(String message) {
        super(message);
    }

    public ApiControllerException(String message, RtnCode code) {
        super(message, code);
    }
}
