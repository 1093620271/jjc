package com.jjc.comm.common.util;


import com.jjc.comm.common.sys.BackMsg;

public class BackMsgUtil {

	public static BackMsg buildMsg(boolean isSuccess, String retMsg){
		BackMsg backMsg = new BackMsg();
		backMsg.setSuccess(isSuccess);
		backMsg.setMsg(retMsg);
		return backMsg;
	}

}
