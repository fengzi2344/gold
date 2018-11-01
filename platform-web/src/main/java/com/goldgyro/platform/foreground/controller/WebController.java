package com.goldgyro.platform.foreground.controller;

import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.entity.VersionInfo;
import com.goldgyro.platform.core.client.service.CustomerService;
import com.goldgyro.platform.core.client.service.VersionInfoService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import com.goldgyro.platform.foreground.annotation.GoldAnnotation;
import com.xiaoleilu.hutool.http.HttpUtil;
import com.xiaoleilu.hutool.json.JSONObject;
import com.xiaoleilu.hutool.util.ReUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WebController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private VersionInfoService versionInfoService;

    /**
     * 分享注册
     * @param inviteMobile
     * @param model
     * @return
     */
    @GetMapping("/toRegist")
    @GoldAnnotation
    public String toRegist(String inviteMobile,Model model){
        if(customerService.findByCustMobile(inviteMobile)==null){
            model.addAttribute("error","分享用户不存在！");
            return "error";
        }
        model.addAttribute("inviteMobile",inviteMobile);
        return "regist";
    }
    @ResponseBody
    @GetMapping("/queryBank")
    public InterfaceResponseInfo queryBank(String bankNo){
        //银行代码请求接口 url
        String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo="+bankNo+"&cardBinCheck=true";
        //发送请求，得到 josn 类型的字符串
        String result = HttpUtil.get(url);
        // 转为 Json 对象
        JSONObject json = new JSONObject(result);
        //获取到 bank 代码
        String bank = String.valueOf(json.get("bank"));
        //爬取支付宝银行合作商页面
        String listContent = HttpUtil.get("http://ab.alipay.com/i/yinhang.htm","gb2312");
        //过滤得到需要的银行名称
        List<String> titles = ReUtil.findAll("<span title=\"(.*?)\" class=\"icon "+bank+"\">(.*?)</span>", listContent, 2);
        Map map = new HashMap();
        if(titles.size()>0) {
            map.put("bankName",titles.get(0));
            return new InterfaceResponseInfo("1","成功",map);
        }else {
            return new InterfaceResponseInfo("1","成功",null);
        }
    }
    @RequestMapping("/return_url")
    public String returnUrl(HttpServletRequest request,Model model){
        String tradeStatus = request.getParameter("trade_status");
        if("TRADE_SUCCESS".equals(tradeStatus)){
            model.addAttribute("status","支付成功！");
        }else{
            model.addAttribute("status","支付失败！");
        }
        return "vip";
    }
    @RequestMapping("/toDownload")
    public String toDownload(Model model){
        VersionInfo versionInfo = versionInfoService.findNewestOne();
        model.addAttribute("versionInfo",versionInfo);
        return "goldgyro/download";
    }
    @GetMapping("/lottery")
    public String lottery(@RequestParam(value = "n",required = false) String number, Model model){
        if(number != null && number.length()==3){
            model.addAttribute("num",number);
        }
        return "lottery";
    }
}
