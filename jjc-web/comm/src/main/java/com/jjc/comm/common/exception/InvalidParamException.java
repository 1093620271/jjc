package com.jjc.comm.common.exception;

/**
 * Created by Administrator on 2017/9/12.
 */
public class InvalidParamException extends BaseRuntimeException {
    public InvalidParamException(String message) {
        super(message);
    }

    public InvalidParamException(String message, RtnCode code) {
        super(message, code);
    }
}
