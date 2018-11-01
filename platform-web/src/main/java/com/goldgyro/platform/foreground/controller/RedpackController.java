package com.goldgyro.platform.foreground.controller;

import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.entity.Redpack;
import com.goldgyro.platform.core.client.service.CustomerService;
import com.goldgyro.platform.core.client.service.RedpackService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/redpack")
public class RedpackController {

    @Autowired
    private RedpackService redpackService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/packNum/{custId}")
    public InterfaceResponseInfo packNum(@PathVariable String custId){
        Redpack redpack = redpackService.findByCustId(custId);
        Map<String,Integer> map = new HashMap<>();
        if(redpack==null || redpack.getUnpackNum() == null){
            map.put("packNum",0);
        }else{
            map.put("packNum",redpack.getUnpackNum());
        }
        return new InterfaceResponseInfo("1","请求成功",map);
    }

    @GetMapping("/unpack/{custId}")
    public synchronized InterfaceResponseInfo unpackNum(@PathVariable String custId){

        Customer customer = customerService.findByCustId(custId);
        if(!"AUTH".equals(customer.getCustStatus())){
            return new InterfaceResponseInfo("0","您尚未实名认证！",null);
        }
        Redpack redpack = redpackService.findByCustId(custId);
        if(redpack == null || redpack.getUnpackNum() == null || redpack.getUnpackNum()<=0){
            return new InterfaceResponseInfo("0","红包数量不足！",null);
        }
        int[] result = redpackService.doUnpack(redpack);
        Map<String,Integer> map = new HashMap<>();
        map.put("money",result[0]);
        map.put("packNum",result[1]);
        return new InterfaceResponseInfo("1","请求成功",map);
    }

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        for(int i =0;i<984;i++){
            restTemplate.getForEntity("http://www.tuoluo718.com/redpack/unpack/20180829110833694370", Object.class);
        }
    }
}
