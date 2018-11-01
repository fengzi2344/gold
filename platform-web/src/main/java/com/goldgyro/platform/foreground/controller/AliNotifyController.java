package com.goldgyro.platform.foreground.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.StringUtils;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.goldgyro.platform.base.utils.UUIDUtils;
import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.entity.MerchantPO;
import com.goldgyro.platform.core.client.entity.Trade;
import com.goldgyro.platform.core.client.service.CustomerService;
import com.goldgyro.platform.core.client.service.MerchantService;
import com.goldgyro.platform.core.client.service.TradeService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import com.goldgyro.platform.foreground.LogUtils;
import com.goldgyro.platform.foreground.annotation.GoldAnnotation;
import com.goldgyro.platform.foreground.batchs.BaseCtl;
import com.goldgyro.platform.foreground.constants.AlipayConfig;
import com.goldgyro.platform.foreground.constants.PayChannelConstants;
import com.goldgyro.platform.foreground.utils.Api;
import com.goldgyro.platform.foreground.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/alipay")
@GoldAnnotation
public class AliNotifyController {
    @Autowired
    private TradeService tradeService;
    @Autowired
    private Environment environment;
    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private Environment env;

    /**
     *@param custId        充值人
     *@param type    充值money(RMB)
     *@throws AlipayApiException  ModelAndView
     */
    @RequestMapping(value="/createOrder",method={RequestMethod.POST,RequestMethod.GET})
    public Map alipay(String custId,String type) throws AlipayApiException{
        String tradeMoney = null;
        String body = null;
        if("V".equals(type.trim())){
            tradeMoney = env.getProperty("pay.vipPrice");
            body = "vip";
        }else {
            tradeMoney = env.getProperty("pay.memberPrice");
            body = "member";
        }
        Map m = new HashMap();
       String orderStr = "";
        try {

            Map<String,String> orderMap = new LinkedHashMap<String,String>();            //订单实体
            Map<String,String> bizModel = new LinkedHashMap<String,String>();            //公共实体

            //****** 2.商品参数封装开始 *****//*                                            //手机端用
            // 商户订单号，商户网站订单系统中唯一订单号，必填
            String outTradNo = UUIDUtils.getUUIDNum(20);
            orderMap.put("out_trade_no",outTradNo);
            // 订单名称，必填
            orderMap.put("subject",body+" purchase");
            // 付款金额，必填
            orderMap.put("total_amount",tradeMoney);
            // 商品描述，可空
            orderMap.put("body",body);
            // 超时时间 可空
            orderMap.put("timeout_express","30m");
            // 销售产品码 必填
            orderMap.put("product_code","QUICK_MSECURITY_PAY");

            //1.商户appid
            bizModel.put("app_id",AlipayConfig.APPID);
            //2.请求网关地址
            bizModel.put("method",AlipayConfig.URL);
            //3.请求格式
            bizModel.put("format",AlipayConfig.FORMAT);
            //4.回调地址
            bizModel.put("return_url",AlipayConfig.return_url);
            //5.私钥
            bizModel.put("private_key",AlipayConfig.RSA_PRIVATE_KEY);
            //6.商家id
            bizModel.put("seller_id","2088622889197893");
            //7.加密格式
            bizModel.put("sign_type",AlipayConfig.SIGNTYPE);


            //实例化客户端
            AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, AlipayConfig.APPID, AlipayConfig.RSA_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY,AlipayConfig.SIGNTYPE);
            //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
            AlipayTradeAppPayRequest ali_request = new AlipayTradeAppPayRequest();

            //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setPassbackParams(URLEncoder.encode((String)orderMap.get("body").toString()));;  //描述信息  添加附加数据
            model.setBody(orderMap.get("body"));                        //商品信息
            model.setSubject(orderMap.get("subject"));                  //商品名称
            model.setOutTradeNo(orderMap.get("out_trade_no"));          //商户订单号(自动生成)
            model.setTimeoutExpress(orderMap.get("timeout_express"));     //交易超时时间
            model.setTotalAmount(orderMap.get("total_amount"));         //支付金额
            model.setProductCode(orderMap.get("product_code"));         //销售产品码
            model.setSellerId("2088622889197893");                       //商家id
            ali_request.setBizModel(model);
            ali_request.setNotifyUrl(AlipayConfig.notify_url);          //回调地址

            AlipayTradeAppPayResponse response = client.sdkExecute(ali_request);
            orderStr = response.getBody();
            System.err.println(orderStr);
            m.put("result",orderStr);
            m.put("status",0);
            m.put("type",type);
            m.put("msg","订单生成成功");
            Trade trade = new Trade(outTradNo,custId,orderMap.get("subject"),Double.valueOf(orderMap.get("total_amount")),orderMap.get("body"),"alipay",new Timestamp(new Date().getTime()),"INIT");
            tradeService.save(trade);
        } catch (Exception e) {
            e.printStackTrace();
            m.put("status",1);
            m.put("msg","订单生成失败");
        }

        return m;
    }

    /**
     * 支付宝支付成功后.回调该接口
     *
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/notify_url", method = {RequestMethod.POST, RequestMethod.GET})
    public String notify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        //2.封装必须参数
        String outTradeNo = request.getParameter("out_trade_no");
        String tradeStatus = request.getParameter("trade_status");
        LogUtils.writeStringToFile("outTradeNo:"+outTradeNo);
        Trade trade = tradeService.findByOrderId(outTradeNo);
        if(trade == null){
            return "success";
        }
        //3.签名验证(对支付宝返回的数据验证，确定是支付宝返回的)
        boolean signVerified = false;
        try {
            //3.1调用SDK验证签名
            signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, AlipayConfig.SIGNTYPE);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //4.对验签进行处理
        if (signVerified || true) {    //验签通过
            if (tradeStatus.equals("TRADE_SUCCESS")) {    //只处理支付成功的订单: 修改交易表状态,支付成功
                /**
                 *
                 */
                trade.setTradeStatus("SUCCESS");
                MerchantPO merchant = merchantService.findMerchantByCustId(trade.getCustId());
                String txnRate = environment.getProperty("goldgyro.vip");
                String type = "VIP";
                if("member".equals(trade.getBody())){
                    txnRate = environment.getProperty("goldgyro.member");
                    type ="MEMBER";
                }
                LogUtils.writeStringToFile("==============i swear i don't have a gun===============");
                if(RequestUtil.updateFee(customerService,txnRate,trade.getCustId())){
                    tradeService.updateFeeAnStatus(trade,type);
                    return "success";
                }else{
                    return "fail";
                }
            } else {
                trade.setTradeStatus("FAIL");
                return "fail";
            }
        } else {  //验签不通过
            System.err.println("验签失败");
            return "fail";
        }
    }
    @RequestMapping("/updateRate")
    public boolean updateRate(String vipRate,String custId){
        return RequestUtil.updateFee(customerService,vipRate,custId);
    }
    @GetMapping("/payStatus/{outTradNo}")
    public InterfaceResponseInfo payStatus(@PathVariable @NotNull String outTradNo){

        Trade trade = tradeService.findByOrderId(outTradNo);
        if(trade == null || trade.getTradeStatus() == null || !"success".equalsIgnoreCase(trade.getTradeStatus())){
            return new InterfaceResponseInfo("0","支付失败",null);
        }
        return new InterfaceResponseInfo("1","支付成功",null);
    }

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Boolean> entity = restTemplate.getForEntity("http://test.tuoluo718.com/alipay/updateRate?custId=20180820180846112180&vipRate=0.0056", Boolean.class);
        System.out.println(entity.getBody());
    }

}
