package com.goldgyro.platform.base.lang;

import org.apache.log4j.Logger;

public enum EnumAuthType {
	TYPE_QQ("QQ登录", 2),
	TYPE_SINA("微博登陆", 1);
	
	Logger logger = Logger.getLogger(EnumAuthType.class);
	
	private String description;
	private int value;
	
	private EnumAuthType(String desc, int value) {
		this.description = desc;
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public int getValue() {
		return value;
	}
	
	public static EnumAuthType getEnumStatus(int type) {
		EnumAuthType[] status = EnumAuthType.values();
		for(int i = 0; i < status.length; i++) {
			if(status[i].getValue() == type) {
				return status[i];
			}
		}
		
		throw new RuntimeException("unsupported enum code!");
	}
}
