package com.goldgyro.platform.core.comm.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.goldgyro.platform.core.client.service.CallBack;

public class SendInfoUtils {
	static Logger logger = Logger.getLogger(SendInfoUtils.class);
	
	/**
	 * 以http—post方式请求
	 * @param paramMap
	 * @param targetUrl
	 * @param encode
	 * @param callB
	 * @param failParams
	 * @param accessParams
	 */
	public static String post(Map<String, String> paramMap, String targetUrl, String encode, CallBack callB, Object failParams, Object accessParams) {
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		for(String key : paramMap.keySet()) {
			formParams.add(new BasicNameValuePair(key, paramMap.get(key)));
		}
		
		try {
			SendInfoUtils.post(formParams, targetUrl, encode, callB, failParams, accessParams);
		} catch (Exception e) {
			logger.error("接口请求发生异常："+targetUrl, e);
			return "FAIL";
		}
		
		return "OK";
	}
	
	/**
	 * 以http—post方式请求
	 * @param params
	 * @param targetUrl
	 * @param encode
	 * @param callB
	 * @param failParams
	 * @param accessParams
	 */
	public static void post(List<NameValuePair> params, String targetUrl, String encode, CallBack callB, Object failParams, Object accessParams) throws Exception {
		logger.info("======================外部接口ulr====================================================="+targetUrl);
		
		CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
		httpClient.start();
		HttpPost requestPost = new HttpPost(targetUrl);
		
		requestPost.setEntity(new UrlEncodedFormEntity(params, encode));
		httpClient.execute(requestPost, new FutureCallback<HttpResponse>() {
			public void failed(Exception t) {
				try {
					if(null != callB) {
						callB.failCallBack(failParams);
					}
				}catch(Exception ex) {
					logger.error("外部接口请求失败的后续处理发生异常！回调类："+callB.getClass().getName(), t);
				}
				logger.error("外部接口请求失败！", t);
			}

			public void completed(HttpResponse resp) {
				try {
					InputStream stram = resp.getEntity().getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(stram));
					System.out.println(reader.readLine());
					byte[] bs = reader.readLine().getBytes();
					logger.info("source info****************:"+bs.toString());
			        String targetInfo = new String(bs, "UTF-8");  
			        logger.info("target info*******************:"+targetInfo);
			       /* if(null != callB) {
						callB.accessCallBack(accessParams);
					}*/
					logger.info("外部接口请求成功！");
				} catch (UnsupportedOperationException e) {
					logger.error("指定的接口路径错误！", e);
				} catch (IOException e) {
					logger.error("IO异常！", e);
				} catch(Exception e) {
					logger.error("外部接口请求失败的后续处理发生异常！回调类："+callB.getClass().getName(), e);
				}
			}

			public void cancelled() {
			}
		});
	}
}
