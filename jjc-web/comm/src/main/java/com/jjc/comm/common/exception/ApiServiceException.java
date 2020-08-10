package com.jjc.comm.common.exception;

/**
 * @author huoquan
 * @date 2017/12/19.
 */
public class ApiServiceException extends BaseRuntimeException{
    public ApiServiceException(String message) {
        super(message);
    }

    public ApiServiceException(String message, RtnCode code) {
        super(message, code);
    }
}
