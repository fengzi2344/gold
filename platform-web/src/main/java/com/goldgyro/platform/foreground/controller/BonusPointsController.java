package com.goldgyro.platform.foreground.controller;

import com.goldgyro.platform.core.client.entity.BonusPoints;
import com.goldgyro.platform.core.client.service.BonusPointsDetailService;
import com.goldgyro.platform.core.client.service.BonusPointsService;
import com.goldgyro.platform.core.client.service.PaymentPlanService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bonus")
public class BonusPointsController {
    @Autowired
    private BonusPointsService bonusPointsService;
    @Autowired
    private BonusPointsDetailService bonusPointsDetailService;

    @Autowired
    private PaymentPlanService paymentPlanService;
    @RequestMapping("")
    public InterfaceResponseInfo bonus(String custId){
        BonusPoints bonusPoints = bonusPointsService.findByCustId(custId);
        if(bonusPoints == null){
            bonusPoints = new BonusPoints();
            bonusPoints.setSum(0);
        }
        return new InterfaceResponseInfo("1","成功",bonusPoints);
    }
    @RequestMapping("/detail")
    private InterfaceResponseInfo detail(String bonusId){
        return new InterfaceResponseInfo("1","成功",bonusPointsDetailService.findByBonus(bonusId));
    }
}
