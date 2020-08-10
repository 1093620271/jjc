package com.jjc.comm.common.sys;

import java.io.Serializable;


public class BackMsg implements Serializable {

	private static final long serialVersionUID = 8705195763448316698L;

	private boolean success = false;
	private String msg = "";
	private String backCode = "";
	private Object data = null;

	public void clear(){
		success = false;
		msg = "";
		backCode = "";
		data = null;
	}

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getBackCode() {
		return backCode;
	}
	public void setBackCode(String backCode) {
		this.backCode = backCode;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
