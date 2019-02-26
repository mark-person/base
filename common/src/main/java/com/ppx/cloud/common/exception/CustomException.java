package com.ppx.cloud.common.exception;


/**
 * 非法URL异常
 * uri长度>64 uri带有. 
 * @author mark
 * @date 2018年12月24日
 */
@SuppressWarnings("serial")
public class CustomException extends RuntimeException {
	
	private int errcode;
	
	private String errmsg;
	
	public CustomException(int errcode, String errmsg) {
		super(errmsg);
		this.errcode = errcode;
		this.errmsg = errmsg;
	}

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
}


