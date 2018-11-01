package com.goldgyro.platform.core.client.service;

/**
 * 回调接口
 * @author wg2993
 * @version 2018/07/17
 */
public interface CallBack {
	void accessCallBack(Object paramObj);
	void failCallBack(Object paramObj);
}
