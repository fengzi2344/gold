package com.goldgyro.platform.foreground.wechat;

import com.goldgyro.platform.base.utils.UUIDUtils;
import com.goldgyro.platform.core.client.entity.Trade;
import com.goldgyro.platform.core.client.service.CustomerService;
import com.goldgyro.platform.core.client.service.TradeService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import com.goldgyro.platform.foreground.annotation.GoldAnnotation;
import com.goldgyro.platform.foreground.utils.RequestUtil;
import com.goldgyro.platform.foreground.wechat.util.DateUtil;
import com.goldgyro.platform.foreground.wechat.util.PayCommonUtil;
import com.goldgyro.platform.foreground.wechat.util.StringUtil;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;


@Controller
@RequestMapping("/wechat")
@GoldAnnotation
public class PayController {
    String randomString = PayCommonUtil.getRandomString(32);
    //支付成功后的回调函数
    @Autowired
    private Environment env;
    public PayController() {
        System.out.println("MainController构造函数");
    }
    @Autowired
    private TradeService tradeService;
    @Autowired
    private CustomerService customerService;

    /**
     * @param request
     * @return
     */
    @RequestMapping(value = "/wxpay", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public InterfaceResponseInfo wxpay(HttpServletRequest request) {
        String totalFee = null;
        String body = null;
        String type = request.getParameter("type");
        if("V".equalsIgnoreCase(type)){
            totalFee = env.getProperty("pay.vipPrice");
            body = "vip";
        }else {
            totalFee = env.getProperty("pay.memberPrice");
            body = "member";
        }
        BigDecimal totalAmount = new BigDecimal(totalFee);
        String tradeNo = UUIDUtils.getUUIDNum(24);
        String description="merchant upgrade";

        Map<String, String> map = weixinPrePay(tradeNo,totalAmount,description,request);
        SortedMap<String, Object> finalpackage = new TreeMap<String, Object>();
        //应用ID
        finalpackage.put("appid", env.getProperty("pay.wechat.WXAppID"));
        //商户号
        finalpackage.put("partnerid", env.getProperty("pay.wechat.MCH_ID"));
        Long time = (System.currentTimeMillis() / 1000);
        //时间戳
        finalpackage.put("timestamp", time.toString());
        //随机字符串
        finalpackage.put("noncestr", map.get("nonce_str"));
        //预支付交易会话ID
        finalpackage.put("prepayid", map.get("prepay_id"));
        //扩展字段
        finalpackage.put("package", "Sign=WXPay");
        //sign
        finalpackage.put("sign",PayCommonUtil.createSign("UTF-8",finalpackage,env.getProperty("pay.wechat.API_KEY")));
        Trade trade = new Trade(tradeNo,request.getParameter("custId"),"V".equalsIgnoreCase(type)?"vip purchase":"member purchase",Double.valueOf(totalFee),body,"wechatPay",new Timestamp(new Date().getTime()),"INIT");
        tradeService.save(trade);
        return new InterfaceResponseInfo("1","SUCCESS",finalpackage);
    }


    /**
     * 统一下单
     * 应用场景：商户系统先调用该接口在微信支付服务后台生成预支付交易单，返回正确的预支付交易回话标识后再在APP里面调起支付。
     * @param tradeNo
     * @param totalAmount
     * @param description
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> weixinPrePay(String tradeNo,BigDecimal totalAmount,
                                            String description,HttpServletRequest request) {
        SortedMap<String, Object> parameterMap = new TreeMap<String, Object>();
        BigDecimal total = totalAmount.multiply(new BigDecimal(100));  //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        java.text.DecimalFormat df=new java.text.DecimalFormat("0");
        parameterMap.put("appid", env.getProperty("pay.wechat.WXAppID"));
        parameterMap.put("mch_id", env.getProperty("pay.wechat.MCH_ID"));
        parameterMap.put("nonce_str", randomString);
        parameterMap.put("body", description);
        parameterMap.put("out_trade_no", tradeNo);
        parameterMap.put("fee_type", "CNY");
        parameterMap.put("total_fee", df.format(total));
        parameterMap.put("spbill_create_ip", PayCommonUtil.getRemoteHost(request));
        parameterMap.put("notify_url", env.getProperty("domainUrl")+"/wechat/notifyWeiXinPay");
        parameterMap.put("trade_type", "APP");//"JSAPI"
        String apiKey = env.getProperty("pay.wechat.API_KEY");
        String sign = PayCommonUtil.createSign("UTF-8", parameterMap,apiKey);
        parameterMap.put("sign", sign);
        String requestXML = PayCommonUtil.getRequestXml(parameterMap);
        System.out.println(requestXML);
        String result = PayCommonUtil.httpsRequest(
                "https://api.mch.weixin.qq.com/pay/unifiedorder", "POST",
                requestXML);
        System.out.println(result);
        Map<String, String> map = null;
        try {
            map = PayCommonUtil.doXMLParse(result);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 此函数会被执行多次，如果支付状态已经修改为已支付，则下次再调的时候判断是否已经支付，如果已经支付了，则什么也执行
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws JDOMException
     */
    @RequestMapping(value = "/notifyWeiXinPay", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String notifyWeiXinPay(HttpServletRequest request, HttpServletResponse response) throws IOException, JDOMException {
        System.out.println("微信支付回调");
        InputStream inStream = request.getInputStream();
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        String resultxml = new String(outSteam.toByteArray(), "utf-8");
        Map<String, String> params = PayCommonUtil.doXMLParse(resultxml);
        outSteam.close();
        inStream.close();

        String apiKey = env.getProperty("pay.wechat.API_KEY");
        Map<String,String> return_data = new HashMap<String,String>();
        if (!PayCommonUtil.isTenpaySign(params,apiKey)) {
            // 支付失败
            return_data.put("return_code", "FAIL");
            return_data.put("return_msg", "return_code不正确");
            return StringUtil.GetMapToXML(return_data);
        } else {
            System.out.println("===============付款成功==============");
            // ------------------------------
            // 处理业务开始
            // ------------------------------
            // 此处处理订单状态，结合自己的订单数据完成订单状态的更新
            // ------------------------------

            String total_fee = params.get("total_fee");
            String type = (env.getProperty("pay.vipPrice")+"00").equals(total_fee)?"VIP":"MEMBER";
            String txnRate = "";
            if("VIP".equals(type)){
                txnRate = env.getProperty("goldgyro.vip");
            }else {
                txnRate = env.getProperty("goldgyro.member");
            }
            double v = Double.valueOf(total_fee);
            String out_trade_no = String.valueOf(params.get("out_trade_no").split("O")[0]);
            return_data.put("return_code", "SUCCESS");
            return_data.put("return_msg", "OK");
            Trade trade = tradeService.findByOrderId(out_trade_no);
            if(trade!= null && !"SUCCESS".equals(trade.getTradeStatus())){
                trade.setTradeStatus("SUCCESS");
                if(RequestUtil.updateFee(customerService,txnRate,trade.getCustId())){
                    tradeService.updateFeeAnStatus(trade,type);
                }
            }
            return StringUtil.GetMapToXML(return_data);
        }
    }

}
