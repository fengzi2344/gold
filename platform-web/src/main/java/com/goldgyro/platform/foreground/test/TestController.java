package com.goldgyro.platform.foreground.test;

import com.goldgyro.platform.core.client.service.PayCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private PayCardService payCardService;

    @GetMapping("/test")
    public void test() {
        String consumeOrderId = "201808081108164443214680";
        String orderStatus = "01";
        payCardService.doProcessAfter(consumeOrderId,orderStatus);
    }
}
