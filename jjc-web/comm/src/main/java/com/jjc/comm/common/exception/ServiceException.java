package com.jjc.comm.common.exception;

/**
 * Created by Administrator on 2017/9/12.
 */
public class ServiceException extends BaseRuntimeException {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, RtnCode code) {
        super(message, code);
    }
}
