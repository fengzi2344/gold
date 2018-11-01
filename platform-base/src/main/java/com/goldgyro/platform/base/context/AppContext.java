package com.goldgyro.platform.base.context;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

/**
 * 系统配置工具
 * @author wg2993
 * @version 2018/7/4
 */
@Component
public class AppContext implements ServletContextAware {
	
	/*
	 * 系统配置信息
	 * - 在 StartupListener 类中加载
	 */
	public Map<String, String> config;

	/**
	 * 容器全局变量
	 */
	private ServletContext servletContext;

	public Map<String, String> getConfig() {
		return config;
	}

	public void setConfig(Map<String, String> config) {
		this.config = config;
		//同步更新容器全局变量
		Iterator<Map.Entry<String, String>> iter = config.entrySet().iterator();
		while (iter.hasNext()){
			Map.Entry<String, String> e = iter.next();
			servletContext.setAttribute(e.getKey(), e.getValue());
		}
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}
}
