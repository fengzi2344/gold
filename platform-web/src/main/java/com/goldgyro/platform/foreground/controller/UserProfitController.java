package com.goldgyro.platform.foreground.controller;

import com.goldgyro.platform.core.client.domain.CustIncome;
import com.goldgyro.platform.core.client.entity.CustIncomeDetailPO;
import com.goldgyro.platform.core.client.entity.CustIncomePO;
import com.goldgyro.platform.core.client.service.CustIncomeDetailService;
import com.goldgyro.platform.core.client.service.CustIncomeService;
import com.goldgyro.platform.core.client.service.CustomerService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/userProfit")
public class UserProfitController {
   @Autowired
   private CustIncomeService custIncomeService;

   @Autowired
   private CustIncomeDetailService custIncomeDetailService;

   @Autowired
   private CustomerService customerService;

    /**
     * 钱包首页数据接口
     * @param custId
     * @return
     */
   @RequestMapping("/totalIncome/{custId}")
   public InterfaceResponseInfo getTotalIncome(@PathVariable String custId){
       if(StringUtils.isEmpty(custId)){
            return new InterfaceResponseInfo("0","参数错误",null);
        }
       Map<String,Object> incomeMap = custIncomeDetailService.findIncomeInfo(custId);
       return new InterfaceResponseInfo("1","成功",incomeMap);
   }
   @PostMapping("/tradeDetail")
    public InterfaceResponseInfo tradeAnalysisByDay(String custId,String analysisType){
       if(StringUtils.isEmpty(custId) || StringUtils.isEmpty(analysisType)){
           return new InterfaceResponseInfo("0","参数错误",null);
       }
       List<Map<String,Object>> incomeList = custIncomeDetailService.findTradeDetail(custId,analysisType);
       return new InterfaceResponseInfo("1","成功",incomeList);
    }
    @GetMapping("/customerAnalysis")
    public InterfaceResponseInfo customerAnalysis(String custId){

       if(StringUtils.isEmpty(custId)){
           return new InterfaceResponseInfo("0","参数错误",null);
       }
       Map<String, Object> map = customerService.queryCustomerAnaysisData(custId);
       return new InterfaceResponseInfo("1","成功", map);
    }

}
