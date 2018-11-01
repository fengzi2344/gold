package com.goldgyro.platform.foreground.controller;

import com.goldgyro.platform.base.utils.MD5;
import com.goldgyro.platform.base.utils.NumberUtils;
import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.service.CustomerService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import com.goldgyro.platform.core.comm.utils.SMSUtils;
import com.goldgyro.platform.foreground.annotation.GoldAnnotation;
import com.goldgyro.platform.foreground.constants.MessageConstant;
import com.goldgyro.platform.foreground.redisService.RedisService;
import com.goldgyro.platform.foreground.wechat.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 登录页
 * 
 * @author wg2993
 */
@RestController
public class LoginController extends BaseController {
	Logger logger = Logger.getLogger(LoginController.class);
	private static final String SALT = "JinTuoLuo";
	@Autowired
	private CustomerService custService;

	@Autowired
	private RedisService redisService;

	@Value("${expire}")
	private Integer expire;
	
	/**
	 * 跳转登录页
	 * 
	 * @return
	 */
	/*
	 * @RequestMapping(value = "/login", method = RequestMethod.GET) public String
	 * view() { return getView(Views.LOGIN); }
	 */

	/**
	 * 提交登录
	 * @param loginIdent
	 * @param password
	 * @param rememberMe
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
    @GoldAnnotation
	public InterfaceResponseInfo login(@RequestParam(value="loginIdent", required=false) String loginIdent,
			@RequestParam(value = "password", required=false) String password,
			@RequestParam(value = "rememberMe", defaultValue = "0") int rememberMe) {
		if (StringUtils.isBlank(loginIdent) || StringUtils.isBlank(password)) {
			logger.error(MessageConstant.CUST_ERROR_CODE_001);
			return new InterfaceResponseInfo(MessageConstant.FAIL,
					MessageConstant.getMessage(MessageConstant.CUST_ERROR_CODE_001), null);
		}
		Customer customer = custService.findByCustMobile(loginIdent);
		if(customer == null){
			logger.error("用户不存在"+loginIdent);
			return new InterfaceResponseInfo(MessageConstant.FAIL, "用户不存在", null);
		}
		if(!MD5.md5(password).equals(customer.getCustPassword())){
			logger.error("密码错误"+password);
			return new InterfaceResponseInfo(MessageConstant.FAIL,"密码错误", null);
		}
		if(StringUtils.isEmpty(customer.getIdCardNo()) ||customer.getIdCardNo() == null){
			customer.setIdCardNo("1");
		}
		processToken(customer);
		return new InterfaceResponseInfo(MessageConstant.OK, MessageConstant.getMessage(MessageConstant.OK), customer);
		/*AuthenticationToken token = createToken(loginIdent, password);
		if (token == null) {
			logger.error(MessageConstant.CUST_ERROR_CODE_002);
			return new InterfaceResponseInfo(MessageConstant.FAIL,
					MessageConstant.getMessage(MessageConstant.CUST_ERROR_CODE_002), null);
		}

		if (rememberMe == 1) {
			((UsernamePasswordToken) token).setRememberMe(true);
		}

		try {
			SecurityUtils.getSubject().login(token);

			return new InterfaceResponseInfo(MessageConstant.OK, MessageConstant.getMessage(MessageConstant.OK), custService.findByCustMobile(loginIdent));
		} catch (AuthenticationException e) {
			String msg = null;
			if (e instanceof LockedAccountException) {
				msg = MessageConstant.CUST_ERROR_CODE_004;
			} else {
				msg = MessageConstant.CUST_ERROR_CODE_003;
			}
			logger.error(msg, e);
			return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(msg), null);
		}*/
	}

	private void processToken(Customer customer) {
		String token = redisService.get(customer.getCustId());
		if(StringUtils.isEmpty(token)){
			token = generateToken(customer.getCustId(),customer.getId());
			redisService.set(token,customer.getCustId());
			redisService.set(customer.getCustId(),token);
			redisService.expire(token,expire);
		}else {
			redisService.del(token);
			redisService.del(customer.getCustId());
			token = generateToken(customer.getCustId(),customer.getId());
			redisService.set(token,customer.getCustId());
			redisService.set(customer.getCustId(),token);
			redisService.expire(token,expire);
		}
		customer.setToken(token);
	}

	private static String generateToken(String custId, String id) {
		String token = null;
		token = MD5Util.MD5Encode(custId+UUID.randomUUID().toString() +id,"");
		return token;
	}
	@RequestMapping(value = "/identify", method = RequestMethod.POST)
	@ResponseBody
    @GoldAnnotation
	public InterfaceResponseInfo identify(@RequestParam(value="loginIdent", required=false)String loginIdent,
			@RequestParam(value = "identType", defaultValue = "mobileNo") String identType, 
			@RequestParam(value = "checkMobile", defaultValue = "true") boolean checkMobile, HttpServletRequest request) {
		//生成随机数
		
		String code = NumberUtils.genNumbCodes(6);
		
		try {
			if ("mobileNo".equals(identType)) {
				if(checkMobile && null == custService.findByCustMobile(loginIdent)) {
					logger.error("手机号不正确！");
					return new InterfaceResponseInfo(MessageConstant.FAIL, "手机号不正确！", null);
				}
				
				SMSUtils.sendSecurityCode(loginIdent, code);
				logger.info("手机验证码发送请求结束！");
				
				//放入会话
				request.getSession().getServletContext().setAttribute(loginIdent, code);
			} else if ("mobileNo".equals(identType)) {
				logger.info("邮件验证码未开通！");
			}
		}catch(Exception ex) {
			logger.error(MessageConstant.VALIDCODE_ERROR_CODE_001, ex);
			return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.VALIDCODE_ERROR_CODE_001), null);
		}
		logger.info("identifyingCode======="+code);
		
		
		return new InterfaceResponseInfo(MessageConstant.OK, MessageConstant.getMessage(MessageConstant.OK), null);
	}
	@PostMapping("/loginByType")
    @GoldAnnotation
	public InterfaceResponseInfo loginByType(String mobile,String type,String sign){
		if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(type)){
			return new InterfaceResponseInfo("0","手机号码不能为空！",null);
		}
		Customer customer = null;
		/**
		 * 指纹登录
		 */
		if("1".equals(type)){
			customer = custService.findByCustId(sign);
		}else if("2".equals(type)){
			customer = custService.findByCustMobile(mobile);
			String pattern = customer.getPatternPassword();
			if(!encrypt(sign).equals(sign)){
				return new InterfaceResponseInfo("0","手势密码错误！",null);
			}
		}
		if(customer != null){
			if(StringUtils.isEmpty(customer.getIdCardNo()) ||customer.getIdCardNo() == null){
				customer.setIdCardNo("1");
			}
			processToken(customer);
			return new InterfaceResponseInfo("1","登录成功", customer);
		}
		return new InterfaceResponseInfo("0","登录失败！",null);
	}
	@GetMapping("/createPatternPassword")
    @GoldAnnotation
	public InterfaceResponseInfo createPatternPassword(String custId,String patternPassword){
		Customer customer = custService.findByCustId(custId);
		if(customer == null){
			return new InterfaceResponseInfo("0","用户不存在！",null);
		}
		customer.setPatternPassword(encrypt(patternPassword));
		custService.save(customer);
		return new InterfaceResponseInfo("1","设置成功！", customer);
	}
	/**
	 * MD5 加密
	 * SALT 盐值
	 * @param str
	 * @return
	 */
	private String encrypt(String str){
		return MD5.md5(str+SALT);
	}
}
