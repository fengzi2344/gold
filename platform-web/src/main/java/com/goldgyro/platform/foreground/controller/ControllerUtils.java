package com.goldgyro.platform.foreground.controller;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.goldgyro.platform.foreground.constants.MessageConstant;

public class ControllerUtils {
	static Logger logger = Logger.getLogger(ControllerUtils.class);
	//验证码
	public static String validatorIdentifyingCode(HttpServletRequest request, String identifyingCode, String mobileNo) {
		//验证码验证
		Object validCode = request.getSession().getServletContext().getAttribute(mobileNo);
		if(null == validCode) {
			logger.error("请先获取验证码！");
			return MessageConstant.FAIL;
		}
		
		if(StringUtils.isEmpty(identifyingCode) || !identifyingCode.equalsIgnoreCase(validCode.toString())) {
			return MessageConstant.FAIL;
		}
		logger.info("idnetifying========="+validCode.toString()+":"+identifyingCode+":"+identifyingCode.equalsIgnoreCase(validCode.toString()));
		request.removeAttribute(mobileNo);
			
       	return  MessageConstant.OK;
	}
	
	/**
	 * 排险空属性
	 * @param source
	 * @return
	 */
	public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
