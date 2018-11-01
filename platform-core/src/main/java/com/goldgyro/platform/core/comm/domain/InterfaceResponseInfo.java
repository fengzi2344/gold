package com.goldgyro.platform.core.comm.domain;

import java.util.ArrayList;

/**
 * 接口响应信息
 * @author wg2993
 * @version 2018/07/11
 */
public class InterfaceResponseInfo {
	private String code;
	private String message;
	private Object data;
	
	public InterfaceResponseInfo(String code, String message, Object data) {
		super();
		this.code = code;
		this.message = message;
		this.data = data==null?new ArrayList<Object>():data;
	}
	public InterfaceResponseInfo(Object data){
		this.code = "1";
		this.message="请求成功";
		this.data = data;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
