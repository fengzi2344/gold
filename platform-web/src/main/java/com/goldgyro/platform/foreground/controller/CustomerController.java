package com.goldgyro.platform.foreground.controller;

import com.alibaba.fastjson.JSONObject;
import com.goldgyro.platform.base.utils.MD5;
import com.goldgyro.platform.base.utils.NumberUtils;
import com.goldgyro.platform.base.utils.UUIDUtils;
import com.goldgyro.platform.core.client.domain.BankCard;
import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.domain.CustomerVO;
import com.goldgyro.platform.core.client.entity.CustomerPO;
import com.goldgyro.platform.core.client.entity.Dictionary;
import com.goldgyro.platform.core.client.entity.FeeRatePO;
import com.goldgyro.platform.core.client.entity.MerchantPO;
import com.goldgyro.platform.core.client.service.*;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import com.goldgyro.platform.core.comm.utils.SMSUtils;
import com.goldgyro.platform.foreground.batchs.BaseCtl;
import com.goldgyro.platform.foreground.constants.CardConstant;
import com.goldgyro.platform.foreground.constants.MessageConstant;
import com.goldgyro.platform.foreground.utils.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.*;


/**
 * 客户信息处理接口
 *
 * @author wg2993
 * @version 2018/07/09
 */
@RestController
@RequestMapping(value = "/cust")
public class CustomerController extends BaseController {
    Logger logger = Logger.getLogger(CustomerController.class);

    @Autowired
    private CustomerService custService;

    @Autowired
    private BankCardService bCardService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private PayCardService payCardService;

    @Autowired
    private DictionaryService dictionaryService;

    @RequestMapping(value = "/queryMerchant", method = RequestMethod.GET)
    @ResponseBody
    public InterfaceResponseInfo queryMerchant(@RequestParam(value = "custId", required = false) String custId) {
        if (null == custId) {
            logger.error("custId不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "客户编号不能为空！", null);
        }

        MerchantPO merchantPO = null;
        try {
            merchantPO = merchantService.findMerchantByCustId(custId);
        } catch (Exception ex) {
            logger.error("通过custID查询客户信息时报错！", ex);
            return new InterfaceResponseInfo(MessageConstant.FAIL, "查询商户信息时出现了错误，请与客服联系！", null);
        }
        return new InterfaceResponseInfo(MessageConstant.OK, "查询成功！", merchantPO);
    }

    /**
     * 通过custID获取客户信息
     *
     * @param custId
     * @return Customer
     */
    @RequestMapping(value = "/queryCust", method = RequestMethod.GET)
    @ResponseBody
    public InterfaceResponseInfo getCust(@RequestParam(value = "custId", required = false) String custId) {
        if (null == custId) {
            logger.error("custId不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "客户编号不能为空！", null);
        }

        Customer cust = null;
        try {
            cust = custService.findByCustId(custId);
        } catch (Exception ex) {
            logger.error("通过custID查询客户信息时报错！", ex);
            return new InterfaceResponseInfo(MessageConstant.FAIL, "查询客户信息时出现了错误，请与客服联系！", null);
        }
        return new InterfaceResponseInfo(MessageConstant.OK, "查询成功！", cust);
    }

    /**
     * 查询客户银行卡信息:
     *
     * @param custId
     * @param cardType:C为信用卡
     * @return
     */
    @RequestMapping(value = "/queryBankCard", method = RequestMethod.GET)
    @ResponseBody
    public InterfaceResponseInfo queryBankCards(@RequestParam(value = "custId", required = false) String custId, @RequestParam(value = "cardType", required = false) String cardType) {
        if (null == custId) {
            logger.error("客户ID不能为空");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "客户编号不能为空！", null);
        }
        Customer customer = custService.findByCustId(custId);
        logger.info(custId + ":" + customer);
        List<BankCard> bankCardList = new ArrayList<BankCard>();
        try {
            List<BankCard> tmpBankCardList = bCardService.findByCustId(custId);
            for (BankCard bCard : tmpBankCardList) {
                if (!StringUtils.isEmpty(cardType) && cardType.equals(bCard.getBankCardType())) {
                    List<Map<String, Object>> planList = payCardService.findPaymentPlanListByStatus(bCard.getCustId(), bCard.getAccountCode(), "CONFIRMED");
                    if (null != planList && planList.size() > 0) {
                        bCard.setHavePaymentPlan(true);
                    } else {
                        Map<String,Object> latestStatusMap = payCardService.findLatestPlanStatus(bCard.getAccountCode());
                        bCard.setHavePaymentPlan(false);
                        Object planStatus = latestStatusMap.get("status");
                        Object reason = latestStatusMap.get("reason");
                        bCard.setLatestPlanStatus(planStatus==null?"":planStatus.toString());
                        bCard.setFailMsg(reason==null?"":reason.toString());
                    }
                    String icon = BaseCtl.queryBankCode(bCard.getAccountCode());
                    if (!StringUtils.isEmpty(icon)) {
                        bCard.setIcon(CardConstant.ICON_QUERY_URL + icon);
                    }
                    bankCardList.add(bCard);
                }
            }
        } catch (Exception ex) {
            logger.error("通过custID查询客户银行卡信息时报错！", ex);
            return new InterfaceResponseInfo(MessageConstant.FAIL, "查询银行卡信息时出现了错误，请与客服联系！", null);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("bankCardList", bankCardList);
        map.put("idCardNo", (customer == null || customer.getIdCardNo() == null) ? "" : customer.getIdCardNo());
        return new InterfaceResponseInfo(MessageConstant.OK, "查询成功！", map);
    }

    /**
     * 存储：注册和修改
     *
     * @param custMobile
     * @param custPassword
     * @param inviterId
     * @param identifyingCode
     * @param request
     * @return
     */
    @RequestMapping(value = "/save")
    @ResponseBody
    public InterfaceResponseInfo save(@RequestParam(value = "custMobile", required = false) String custMobile,
                                      @RequestParam(value = "custPassword", required = false) String custPassword,
                                      @RequestParam(value = "inviterId", required = false) String inviterId,
                                      @RequestParam(value = "identifyingCode", required = false) String identifyingCode,
                                      HttpServletRequest request) {
        if (custMobile == null) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "电话号码不允许为空", null);
        }
        if (custPassword == null) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "密码不允许为空", null);
        }
        if (inviterId == null) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "邀请人信息不允许为空", null);
        }
        Customer invite = custService.findByCustMobile(inviterId);
        if (null == invite) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "给定的邀请人不存在！", null);
        }

        //验证码验证
        if (MessageConstant.FAIL.equals(ControllerUtils.validatorIdentifyingCode(request, identifyingCode, custMobile))) {
            logger.error(MessageConstant.CUST_ERROR_CODE_022);
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.CUST_ERROR_CODE_022), null);
        }

        Customer customer = custService.findByCustMobile(custMobile);

        // 验证客户信息:如果信息ID为空说明是新增信息，电话号码不能重复
        if (customer != null) {
            logger.error(MessageConstant.CUST_ERROR_CODE_011);
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.CUST_ERROR_CODE_011), null);
        }
        Customer father = recursionInviter(invite.getCustMobile(), 0);
        Customer cust = new Customer();
        cust.setCustId(UUIDUtils.getUUIDNum(20));
        cust.setInviterId(invite.getCustMobile());
        cust.setCustMobile(custMobile);
        cust.setCustPassword(MD5.md5(custPassword));
        cust.setCustLevelSample(CardConstant.NORMAL);
        String levelCode = invite.getLevelCode();
        int childrenNum = custService.findNumByLevelCode(levelCode);
        DecimalFormat df = new DecimalFormat("000");
        String str = df.format(childrenNum + 1);
        cust.setLevelCode(levelCode + str);
        Integer maxCode = custService.findMaxCode()+1;
       /* if(maxCode == 666666 || maxCode == 888888){
            maxCode++;
        }  */
        cust.setCustCode(String.valueOf(maxCode));
        cust.setCustStatus("REG");
        if (father != null && !"NORMAL".equals(father.getCustLevelSample()) && !StringUtils.isEmpty(father.getCustStatus())) {
            cust.setFatherMobile(inviterId);
        }
        try {
            custService.saveAndLinked(cust);
            if (!"888888".equals(invite.getCustMobile())) {
                SMSUtils.sendRegist(invite.getCustMobile(), custMobile);
            }
        } catch (Exception ex) {
            logger.error(MessageConstant.CUST_ERROR_CODE_012);
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.CUST_ERROR_CODE_012), null);
        }
        return new InterfaceResponseInfo(MessageConstant.OK, MessageConstant.getMessage(MessageConstant.OK), null);
    }

    //修改密码
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    @ResponseBody
    public InterfaceResponseInfo updatePassword(@RequestParam(value = "custMobile", required = false) String custMobile,
                                                @RequestParam(value = "custPassword", required = false) String custPassword,
                                                @RequestParam(value = "identifyingCode", required = false) String identifyingCode,
                                                @RequestParam(value = "identType", defaultValue = "mobileNo") String identType, HttpServletRequest request) {
        if (StringUtils.isEmpty(custMobile) || StringUtils.isEmpty(custPassword)) {
            logger.error(MessageConstant.CUST_ERROR_CODE_012);
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.CUST_ERROR_CODE_021), null);
        }

        //验证码验证
        if (MessageConstant.FAIL.equals(ControllerUtils.validatorIdentifyingCode(request, identifyingCode, custMobile))) {
            logger.error(MessageConstant.CUST_ERROR_CODE_022);
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.CUST_ERROR_CODE_022), null);
        }

        //现在只支持手机号
        Customer customer = custService.findByCustMobile(custMobile);

        // 验证客户信息:如果信息ID为空说明是新增信息，电话号码不能重复
        if (customer == null) {
            logger.error(MessageConstant.CUST_ERROR_CODE_011);
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.CUST_ERROR_CODE_011), null);
        }

        try {
            customer.setCustPassword(MD5.md5(custPassword));
            custService.save(customer);
        } catch (Exception ex) {
            logger.error(MessageConstant.CUST_ERROR_CODE_012, ex);
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.CUST_ERROR_CODE_012), null);
        }

        return new InterfaceResponseInfo(MessageConstant.OK, MessageConstant.getMessage(MessageConstant.OK), null);
    }

    /**
     * 实名认证
     *
     * @param cust
     * @param request
     * @return
     */
    @RequestMapping(value = "/cert", method = RequestMethod.POST)
    @ResponseBody
    public InterfaceResponseInfo certification(@RequestBody(required = false) Customer cust, HttpServletRequest request) {
        logger.info("实名认证开始========================");
        // 处理客户信息
        if (cust == null) {
            logger.error(MessageConstant.CUST_ERROR_CODE_010);
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.CUST_ERROR_CODE_010), null);
        }
        logger.info("实名认证========================" + cust.getCustId());
        //验证
        String msg = validatorCust(cust);
        if (!MessageConstant.OK.equals(msg)) {
            logger.error(msg);
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(msg), null);
        }

        Customer customer = null;
        try {
            customer = custService.findByCustId(cust.getCustId());
            if (null == customer) {
                logger.error("此用户未注册，不能做实名认证！");
                return new InterfaceResponseInfo(MessageConstant.FAIL, "此用户未注册，不能做实名认证！", null);
            } else {
                BeanUtils.copyProperties(cust, customer, ControllerUtils.getNullPropertyNames(cust));
                custService.save(customer);
            }
        } catch (Exception ex) {
            logger.error(MessageConstant.CUST_ERROR_CODE_030, ex);
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.CUST_ERROR_CODE_030), null);
        }

        // 处理银行卡信息
        return persitentBankCard(customer, request);

    }

    private InterfaceResponseInfo persitentBankCard(Customer cust, HttpServletRequest request) {
        List<BankCard> bCardList = cust.getBankCards();
        if (null == bCardList || bCardList.size() < 1) {
            logger.info(MessageConstant.CUST_INFO_CODE_030);
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.CUST_INFO_CODE_030), null);
        }
        try {
            BankCard bCard = bCardList.get(0);
            bCard.setOpenningBankProvince("四川");
            bCard.setOpenningBankCity("成都");
            bCard.setOpeningSubBankName("春熙路支行");
            InterfaceResponseInfo msg = validatorCard(bCard);
            if (!MessageConstant.OK.equals(msg.getCode())) {
                logger.error(msg.getMessage());
                return msg;
            }

            if (null != bCardService.findBankCardByCustIdAndAccountCode(bCard.getCustId(), bCard.getAccountCode())) {
                logger.error(MessageConstant.getMessage(MessageConstant.BCARD_ERROR_CODE_001));
                return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.CUST_INFO_CODE_030), null);
            }

            bCard.setIoType(CardConstant.IN_CARD);

            //应做唯一性验证：若已存在收入或支出的默认卡，则不应设置
            if (StringUtils.isEmpty(bCard.getDefaultCard())) {
                bCard.setDefaultCard(CardConstant.YES);
            }
            bCard.setCustId(cust.getCustId());
            bCard.setAccountName(cust.getCustName());
            bCard.setValid("Y");
            bCard.setBindStatus("INIT");
            bCard.setOpenStatus("INIT");
            Map<String, String> map = new HashMap();
            map.put("userName", bCard.getAccountName());
            bCardService.save(bCard, true);
            return new InterfaceResponseInfo(MessageConstant.OK, "实名认证审核中", map);
        } catch (Exception ex) {
            logger.error(MessageConstant.BCARD_ERROR_CODE_003, ex);
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.BCARD_ERROR_CODE_003), null);
        }

    }

    @RequestMapping("/retry/{custId}")
    public InterfaceResponseInfo deleteCard(@PathVariable String custId) {
        try {
            bCardService.deleteByCustId(custId);
            return new InterfaceResponseInfo("1", "成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new InterfaceResponseInfo("0", "失败", null);
        }
    }

    /**
     * 验证器
     *
     * @return boolean
     */
    private String validatorCust(Customer cust) {
        if (StringUtils.isEmpty(cust.getCustId())) {
            return "客户编号不能为空！";
        }
        if (StringUtils.isEmpty(cust.getCustName())) {
            return "客户姓名不能为空！";
        }
        if (StringUtils.isEmpty(cust.getIdCardNo())) {
            return "客户身份证号不能为空！";
        }

        //验证性别
//        if (null != cust.getCustSex() && !"M".equalsIgnoreCase(cust.getCustSex()) && !"G".equalsIgnoreCase(cust.getCustSex())) {
//            return MessageConstant.CUST_ERROR_CODE_040;
//        }

        //验证身份证
        if (null != cust.getCustSex() && !NumberUtils.is18Idcard(cust.getIdCardNo())) {
            return MessageConstant.CUST_ERROR_CODE_041;
        }

        return MessageConstant.OK;
    }

    /**
     * 验证器
     *
     * @return boolean
     */
    private InterfaceResponseInfo validatorCard(BankCard card) {
        if (null == card) {
            logger.error("【绑卡开卡】没接收到银行卡信息！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "没接收到银行卡信息！", null);
        }
        if (StringUtils.isEmpty(card.getAccountCode())) {
            logger.error("【绑卡开卡】银行卡编码不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "银行卡编码不能为空！", null);
        }
        if (StringUtils.isEmpty(card.getAccountName())) {
            logger.error("【绑卡开卡】银行卡账户名称 不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "银行卡账户名称 不能为空！", null);
        }
        if (StringUtils.isEmpty(card.getMobileNo())) {
            logger.error("【绑卡开卡】银行卡预留电话不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "银行卡预留电话不能为空！", null);
        }
		/*if(StringUtils.isEmpty(card.getAccountCode())){
			logger.error("【绑卡开卡】银行名称不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "银行卡名称不能为空！", null);
		}*/
        if (StringUtils.isEmpty(card.getBankCardType())) {
            logger.error("【绑卡开卡】银行卡类型不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "银行卡类型不能为空！", null);
        }
		/*if(StringUtils.isEmpty(card.getAccountCode())){
			logger.error("【绑卡开卡】开户行名称不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "开户行名称不能为空！", null);
		}
		if(StringUtils.isEmpty(card.getAccountCode())){
			logger.error("【绑卡开卡】开户行省份不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "开户行省份不能为空！", null);
		}
		if(StringUtils.isEmpty(card.getAccountCode())){
			logger.error("【绑卡开卡】开户行城市不能为空！");
            return new InterfaceResponseInfo(MessageConstant.FAIL, "开户行城市不能为空！", null);
		}*/


        return new InterfaceResponseInfo(MessageConstant.OK, "银行卡信息验证通过！", null);
    }

    @GetMapping("/unbindCard")
    public InterfaceResponseInfo unbindCard(String cardNo) {
        boolean b = bCardService.findRunningPlan(cardNo);
        if (!b) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "此卡正在交易中，暂不能解绑！", null);
        }
        try {
            bCardService.deleteByCardNo(cardNo);
            return new InterfaceResponseInfo(MessageConstant.OK, "解绑成功！", null);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new InterfaceResponseInfo(MessageConstant.FAIL, "解绑失败！", null);
        }
    }

    private Customer recursionInviter(String mobile, int maxTimes) {
        if (maxTimes > 10) {
            return null;
        }
        Customer customer = custService.findByCustMobile(mobile);
        if (customer == null) {
            return null;
        } else if ("VIP".equals(customer.getCustLevelSample()) || "AGENT".equals(customer.getCustLevelSample())) {
            return customer;
        } else {
            return recursionInviter(customer.getInviterId(), ++maxTimes);
        }
    }

    /**
     * 商户实名认证
     *
     * @param customer
     * @return
     */
    @PostMapping("/registry")
    public InterfaceResponseInfo registry(@RequestBody(required = false) Customer customer) {
        // 处理客户信息
        if (customer == null) {
            logger.error(MessageConstant.CUST_ERROR_CODE_010);
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.CUST_ERROR_CODE_010), null);
        }
        //验证
        String msg = validatorCust(customer);
        if (!MessageConstant.OK.equals(msg)) {
            logger.error(msg);
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(msg), null);
        }
        Customer customerPO = custService.findByCustId(customer.getCustId());
        BankCard bankCard = null;
        List<BankCard> bankCards = customer.getBankCards();
        if (bankCards == null || bankCards.isEmpty()) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, MessageConstant.getMessage(MessageConstant.CUST_INFO_CODE_030), null);
        }
        bankCard = bankCards.get(0);
        if (StringUtils.isEmpty(bankCard.getMobileNo())) {
            bankCard.setMobileNo(customer.getCustMobile());
        }
        customerPO.setIdCardNo(customer.getIdCardNo());
        customerPO.setCustName(customer.getCustName());
        bankCard.setBankUnitNo(bankCard.getOpeningSubBankName());
        bankCard.setCustId(customer.getCustId());
        MerchantPO merchantPO = initMerchant(customer, bankCard);
        Map<String, Object> paramMap = initParamMap(bankCard, customerPO, merchantPO);
        Map<String, Object> headMap = RequestUtil.preHead(RequestUtil.REGISTRY);
        JSONObject json = JSONObject.parseObject(JSONObject.toJSON(headMap).toString());
        paramMap.put("head", json);
        Map<String, Object> resMap = RequestUtil.execute(RequestUtil.REGISTRY, JSONObject.toJSONString(paramMap).trim(), null, headMap.get("orderId").toString());
        String platMerchantCode = null;
        if (resMap == null || resMap.get("platMerchantCode") == null || "".equals((platMerchantCode = resMap.get("platMerchantCode").toString()))) {
            return new InterfaceResponseInfo(MessageConstant.FAIL, "实名认证失败", null);
        }
        merchantPO.setPlatMerchantCode(platMerchantCode);
        merchantPO.setMerchantStatus("REG_SUCCESS");
//        BeanUtils.copyProperties(customer, customerPO);
        customerPO.setCustName(customer.getCustName());
        customerPO.setCustStatus("AUTH");
        merchantService.persist(merchantPO, bankCard, customerPO);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userName", customerPO.getCustName());
        map.put("customer", customerPO);
        return new InterfaceResponseInfo("1", "认证成功", map);
    }

    private Map<String, Object> initParamMap(BankCard bankCard, Customer customer, MerchantPO merchantPO) {
        FeeRatePO fee = payCardService.transRate(customer.getCustLevelSample(), "C");
        String feeRate = "0.0068";
        if (fee != null && fee.getCfeeRate() != 0) {
            feeRate = String.valueOf(fee.getCfeeRate());
        }
        String[] rates = RequestUtil.initRates(feeRate);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("merchantCode", merchantPO.getMerchantCode());
        body.put("merName", merchantPO.getMerchantName());
        body.put("merShortName", merchantPO.getMerShortName());
        body.put("idCardNo", customer.getIdCardNo());
        body.put("phoneno", bankCard.getMobileNo());
        body.put("merAddress", merchantPO.getMerchentAddress());
        body.put("bankAccountNo", bankCard.getAccountCode());
        body.put("bankUnitNo", bankCard.getBankUnitNo() == null ? "303651000000" : bankCard.getBankUnitNo());
        body.put("bankName", bankCard.getBankName());
        body.put("productList", RequestUtil.initProduct(rates));
        body.put("province", bankCard.getOpenningBankProvince());
        body.put("City", bankCard.getOpenningBankCity());
        body.put("withdrawDepositSingleFee", 100);
        body.put("bankAccountName", bankCard.getAccountName());
        return body;
    }

    private MerchantPO initMerchant(Customer customer, BankCard bankCard) {
        MerchantPO merchant = new MerchantPO();
        merchant.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        merchant.setCustId(customer.getCustId());
        merchant.setCity(bankCard.getOpenningBankCity());
        merchant.setProvince(bankCard.getOpenningBankProvince());
        merchant.setMerchantCode(UUIDUtils.getUUIDNum(20));
        String merchantName = getRandomJianHan(new Random().nextInt(4) + 1);
        merchant.setMerchantName(merchantName + "科技股份有限公司");
        merchant.setMerShortName(merchantName);
        merchant.setMerchentAddress("四川省成都市玉林路" + (new Random().nextInt(999) + 1) + "号");
        return merchant;
    }

    public String getRandomJianHan(int len) {
        String ret = "";
        for (int i = 0; i < len; i++) {
            String str = null;
            int hightPos, lowPos; // 定义高低位
            Random random = new Random();
            hightPos = (176 + Math.abs(random.nextInt(39))); // 获取高位值
            lowPos = (161 + Math.abs(random.nextInt(93))); // 获取低位值
            byte[] b = new byte[2];
            b[0] = (new Integer(hightPos).byteValue());
            b[1] = (new Integer(lowPos).byteValue());
            try {
                str = new String(b, "GBK"); // 转成中文
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            ret += str;
        }
        return ret;
    }

    @GetMapping("/ztList")
    public InterfaceResponseInfo ztList(String mobile){
        List<CustomerVO> list = custService.findZtList(mobile);
        return new InterfaceResponseInfo(list);
    }

    @GetMapping("/vipList")
    public InterfaceResponseInfo vipList(String mobile){
        List<CustomerVO> customerPOList = custService.findVipList(mobile);
        return new InterfaceResponseInfo(customerPOList);
    }
    @GetMapping("/directValidList")
    public InterfaceResponseInfo directValidList(String mobile){
        List<CustomerVO> customerVOList = custService.findDirectValidList(mobile);
        return new InterfaceResponseInfo(customerVOList);
    }
    @GetMapping("/updateLevel")
    public String updateLevel() {
        List<CustomerPO> customerList = new ArrayList<>();
        List<CustomerPO> customerPOList = custService.findCustList();
        for (CustomerPO customerPO : customerPOList) {
            findByInviterId(customerList,customerPO);
        }
        if(!customerList.isEmpty()){
            custService.saveList(customerList);
        }
        return "success";
    }
    private void findByInviterId(List<CustomerPO> customerList, CustomerPO customerPO) {
        List<CustomerPO> list = custService.findChilList(customerPO.getCustMobile());
        if(list == null || list.isEmpty()){
            return;
        }else {
            for(int i=0;i<list.size();i++){
                CustomerPO c = list.get(i);
                DecimalFormat df = new DecimalFormat("000");
                String str = df.format(i + 1);
                c.setLevelCode(customerPO.getLevelCode()+str);
                customerList.add(c);
                findByInviterId(customerList,c);
            }
        }
    }
   /* @GetMapping("/testM")
    public String testM(){
        MerchantPO merchantPO = merchantService.findMerchantByCustId("20180824180813393390");
        BankCard bankCard = bCardService.findBankCardById("2018080710081014450736122");
        Customer customerPO = custService.findByCustId("20180824180813393390");
        merchantService.persist(merchantPO, bankCard, customerPO);
        return "success";
    }*/
}
