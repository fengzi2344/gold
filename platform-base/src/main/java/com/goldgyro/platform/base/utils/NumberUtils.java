package com.goldgyro.platform.base.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author wg2993 
 * @version 2018/07/08.
 */
public class NumberUtils {
	/**
	 * 格式化double数据，保留两位小数
	 * @param num 要格式的浮点数
	 * @return
	 */
	public static double doubleFormat(double num) {
		return Double.parseDouble(new DecimalFormat("######0.00").format(num));
	}
	
	/**
	 * 生成数字编码
	 * @param length ： 编码长度
	 * @return
	 */
	public static String genNumbCodes(int length) {
		Random random = new Random();
		
		String result="";
		for (int i=0; i < length; i++)
		{
			result+=random.nextInt(10);
		}
		
		return result;
	}
	
	
	/**
	 * 生成數字和字符組成的編碼
	 * @param length ： 生成的编码长度
	 * @return
	 */
	public static String genCodes(int length){
		String val = "";
		Random random = new Random();
		for(int i = 0; i < length; i++){
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字
			if("char".equalsIgnoreCase(charOrNum)){	// 字符串
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; //取得大写字母还是小写字母
				val += (char) (choice + random.nextInt(26));
			}else if("num".equalsIgnoreCase(charOrNum)){// 数字
				val += String.valueOf(random.nextInt(10));
			}
		}
			
		return val.toLowerCase();
	}

	/**
	 * 验证整数
	 * @param str ： 要验证的数字
	 * @return
	 */
	public static boolean isNumeric(String str){  
	    Pattern pattern = Pattern.compile("[0-9]*");  
	    return pattern.matcher(str).matches();     
	}  
	
	/**
	 * 验证18位身份证:基本数字和位数验证
	 * @param idcard
	 * @return
	 */
	public static boolean is18Idcard(String idcard) {  
        return Pattern  
                .matches(  
                        "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([\\d|x|X]{1})$",  
                        idcard);  
    }  

	/**
	 * 转换成整数
	 * @param obj ： 要转换的整数
	 * @return
	 */
    public static int changeToInt(Object obj) {
        int i = 0;
        if (obj != null && StringUtils.isNumeric(obj.toString())) {
            i = ((Number) obj).intValue();
        }
        return i;
    }

    /**
     * 转换成整数
     * @param ids ： 要转换的数字
     * @return
     */
    public static Set<Long> toList(String ids) {
        Set<Long> ret = new HashSet<>();
        if (StringUtils.isNotBlank(ids)) {
            for (String s : ids.split(",")) {
                long id = org.apache.commons.lang3.math.NumberUtils.toLong(s);
                if (id > 0) {
                    ret.add(id);
                }
            }
        }
        return ret;
    }
}
