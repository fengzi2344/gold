package com.goldgyro.platform.foreground.controller;

import com.goldgyro.platform.core.client.domain.BankCard;
import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.entity.CustIncomeDetailPO;
import com.goldgyro.platform.core.client.entity.CustIncomePO;
import com.goldgyro.platform.core.client.entity.Redpack;
import com.goldgyro.platform.core.client.entity.RedpackDetail;
import com.goldgyro.platform.core.client.service.*;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import com.goldgyro.platform.core.comm.utils.SMSUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jws.Oneway;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/income")
public class IncomeController {
    @Autowired
    private RedpackService redpackService;
    @Autowired
    private CustIncomeService custIncomeService;
    @Autowired
    private CustIncomeDetailService custIncomeDetailService;
    @Autowired
    private RedpackDetailService redpackDetailService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private BankCardService bankCardService;
    @GetMapping("/transfer")
    public synchronized InterfaceResponseInfo transferAccount(String custId,String money){
        Redpack redpack = redpackService.findByCustId(custId);
        BigDecimal b1 = new BigDecimal(redpack.getBalance());
        BigDecimal b2 = new BigDecimal(money);
        if(StringUtils.isEmpty(redpack.getBalance())||b2.compareTo(b1)>0){
            return new InterfaceResponseInfo("0","红包余额不足",null);
        }
        try{
            redpackService.transfer(redpack,money);
            return new InterfaceResponseInfo("1","转账成功",null);
        }catch (Exception e){
            e.printStackTrace();
            return new InterfaceResponseInfo("0","转账失败",null);
        }

    }
    @GetMapping("/withdrawFromIncome")
    public synchronized InterfaceResponseInfo withdrawFromIncome(String custId,String money,String cardNo){
        CustIncomePO custIncome = custIncomeService.findByCustId(custId);
        if(custIncome == null){
            return new InterfaceResponseInfo("0","钱包余额不足",null);
        }
        double amount = Double.valueOf(money.trim());
        if(amount < 10000){
            return new InterfaceResponseInfo("0","提现金额低于100元，暂不可提现",null);
        }
        double balance = custIncome.getTotalIncomeAmt();
        if(balance < amount){
            return new InterfaceResponseInfo("0","钱包余额不足",null);
        }
        try{
            custIncomeService.withdrawFromIncome(custIncome,money,cardNo);
            Customer customer = customerService.findByCustId(custId);
            BankCard bankCard = bankCardService.findBankCardByCustIdAndAccountCode(custId,cardNo);
            DateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
            String date = format.format(new Date());
            String mobileNo = customer.getCustMobile();
            String qian = new BigDecimal(money).divide(new BigDecimal("100")).toString();
            String name = customer.getCustName();
            String bankName = bankCard.getBankName();
            String content1 =name+"||"+bankName+"||"+cardNo+"||"+date+"||"+qian;
            SMSUtils.sendWithdraw1(mobileNo,content1);
            String content2 = name+"||"+qian+"||"+date+"||"+bankName+"||"+cardNo;
            SMSUtils.sendWithdraw2(content2);
            return new InterfaceResponseInfo("1","提现申请成功",null);
        }catch (Exception e){
            return new InterfaceResponseInfo("0","提现申请失败，请联系管理员！",null);
        }

    }

    @GetMapping("/myRedpack")
    public InterfaceResponseInfo myRedpack(String custId){
        Redpack redpack = redpackService.findByCustId(custId);
        if(redpack!=null && StringUtils.isEmpty(redpack.getBalance())){
            redpack.setBalance("0");
        }
        return new InterfaceResponseInfo("1","成功",redpack);
    }
    @RequestMapping("/redPackDetail")
    public InterfaceResponseInfo repackDetail(String redpackId){
        return new InterfaceResponseInfo("1","成功",redpackDetailService.findByRedpackId(redpackId));
    }
    @GetMapping("/incomeDetail")
    public InterfaceResponseInfo incomeDetail(String custId, String time, String type){
        List<CustIncomeDetailPO> detailList = custIncomeDetailService.findDetails(custId,time,type);
        return new InterfaceResponseInfo(detailList);
    }
    @GetMapping("/tradeDetail")
    public InterfaceResponseInfo tradeDetail(String custId){
        List<Map<String,Object>> list = custIncomeService.findTradeDetail(custId);
        return new InterfaceResponseInfo(list);
    }
}
