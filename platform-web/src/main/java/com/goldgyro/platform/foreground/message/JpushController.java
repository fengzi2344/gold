package com.goldgyro.platform.foreground.message;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.Notification;
import com.goldgyro.platform.foreground.utils.JPushClientUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.jpush.api.push.model.notification.PlatformNotification.ALERT;


@RestController
public class JpushController {

    @GetMapping("/push/{custId}")
    public Integer pushMsgToAlias(@PathVariable String custId){
        int i = JPushClientUtil.pushMsg(custId, "test", "测试");
        return i;
    }
    @GetMapping("/pushall")
    public Integer pushMsgToAll(){
        int i = JPushClientUtil.pushMsg("test", "测试");
        return i;
    }
}
