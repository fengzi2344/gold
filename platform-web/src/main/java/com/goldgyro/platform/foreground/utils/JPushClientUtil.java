package com.goldgyro.platform.foreground.utils;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import org.springframework.util.StringUtils;

public class JPushClientUtil {
    private final static String APP_KEY = "c309c8f77348e68bc98c2908";

    private final static String MASTER_SECRET = "26ffa6dba132d1c0abe29773";

    public static int pushMsg(String alias, String title, String msgContent) {
        JPushClient jPushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, ClientConfig.getInstance());
        int result = 0;
        try {
            //PushPayload pushPayload= JpushClientUtil.buildPushObject_all_registrationId_alertWithTitle(registrationId,notification_title,msg_title,msg_content,extrasparam);
            PushPayload pushPayload = null;
            if(!StringUtils.isEmpty(alias)){
                //别名推送
                 pushPayload = buildPushObject_android_and_iosByAlias(alias, title, msgContent);
            }else{
                pushPayload = broadcast(title,msgContent);
            }

            System.out.println(pushPayload);
            PushResult pushResult = jPushClient.sendPush(pushPayload);
            System.out.println(pushResult);
            if (pushResult.getResponseCode() == 200) {
                result = 1;
            }
        } catch (APIConnectionException e) {
            e.printStackTrace();

        } catch (APIRequestException e) {
            e.printStackTrace();
        }

        return result;
    }
    public static int pushMsg( String title, String msgContent) {
        return pushMsg(null,title,msgContent);
    }

    /**
     * registrationId推送
     */
    private static PushPayload buildPushObject_all_registrationId_alertWithTitle(String registrationId, String notification_title, String msg_title, String msg_content, String extrasparam) {

        System.out.println("----------buildPushObject_all_all_alert");
        //创建一个IosAlert对象，可指定APNs的alert、title等字段
        return PushPayload.newBuilder()
                //指定要推送的平台，all代表当前应用配置了的所有平台，也可以传android等具体平台
                .setPlatform(Platform.all())
                //指定推送的接收对象，all代表所有人，也可以指定已经设置成功的tag或alias或该应应用客户端调用接口获取到的registration id
                .setAudience(Audience.registrationId(registrationId))
                //jpush的通知，android的由jpush直接下发，iOS的由apns服务器下发，Winphone的由mpns下发
                .setNotification(Notification.newBuilder()
                        //指定当前推送的android通知
                        .addPlatformNotification(AndroidNotification.newBuilder()

                                .setAlert(notification_title)
                                .setTitle(notification_title)
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra("androidNotification extras key", extrasparam)

                                .build())
                        //指定当前推送的iOS通知
                        .addPlatformNotification(IosNotification.newBuilder()
                                //传一个IosAlert对象，指定apns title、title、subtitle等
                                .setAlert(notification_title)
                                .incrBadge(1)
                                .setSound("sound.caf")
                                .addExtra("iosNotification extras key", extrasparam)

                                .build())
                        .build())
                .setMessage(Message.newBuilder()
                        .setMsgContent(msg_content)
                        .setTitle(msg_title)
                        .addExtra("message extras key", extrasparam)
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(false)
                        .setSendno(1)
                        .setTimeToLive(86400)
                        .build())
                .build();

    }

    /**
     * 标签推送
     */
    private static PushPayload buildPushObject_android_and_iosByTag(String tag, String title, String content) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.tag(tag))
                .setNotification(Notification.newBuilder()
                        .setAlert(content)
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle(title).build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .incrBadge(1)
                                .addExtra(title, content).build())
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(true)
                        .setSendno(1)
                        .setTimeToLive(86400)
                        .build())
                .build();
    }

    /**
     * 别名推送
     */
    private static PushPayload buildPushObject_android_and_iosByAlias(String alias, String title, String content) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.newBuilder()
                        .setAlert(content)
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle(title).build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .incrBadge(1)
                                .addExtra(title, content).build())
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(true)
                        .setSendno(1)
                        .setTimeToLive(86400)
                        .build())
                .build();
    }
    /**
     * 广播
     */
    private static PushPayload broadcast(String title, String content) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
                        .setAlert(content)
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle(title).build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .incrBadge(1)
                                .addExtra(title, content).build())
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(true)
                        .setSendno(1)
                        .setTimeToLive(86400)
                        .build())
                .build();
    }
}
