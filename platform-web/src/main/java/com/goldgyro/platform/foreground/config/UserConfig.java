package com.goldgyro.platform.foreground.config;

import com.goldgyro.platform.foreground.annotation.GoldAnnotation;
import com.goldgyro.platform.foreground.redisService.RedisService;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class UserConfig extends HandlerInterceptorAdapter {
    @Autowired
    private RedisService redisService;
    @Value("${expire}")
    private Integer expire;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if(isGoldAnnotation(o)){
            return true;
        }

        String token = httpServletRequest.getParameter("token");
        if(StringUtils.isEmpty(token)){
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("text/html; charset=utf-8");
            PrintWriter writer = httpServletResponse.getWriter();
            JsonObject json = new JsonObject();
            json.addProperty("code","0");
            json.addProperty("msg","请先登录");
            json.addProperty("data","");
            writer.write(json.toString());
            return false;
        }else{
            boolean b = StringUtils.isEmpty(redisService.get(token));
            if(b){
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.setContentType("text/html; charset=utf-8");
                PrintWriter writer = httpServletResponse.getWriter();
                JsonObject json = new JsonObject();
                json.addProperty("code","0");
                json.addProperty("msg","登录超时");
                json.addProperty("data","");
                writer.write(json.toString());
                return false;
            }
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String token = request.getParameter("token");
        if(StringUtils.isEmpty(token)){
            return;
        }
        if(StringUtils.isEmpty(redisService.get(token))){
            return;
        }
        redisService.expire(token,Integer.valueOf(expire));
    }

    /**
     * 判断类或方法是否有@GoldAnnotation标识
     * @param handler
     * @return
     */
    private boolean isGoldAnnotation(Object handler) {

        System.out.println(handler.getClass());
        HandlerMethod handler1 = (HandlerMethod) handler;
        Object bean = handler1.getBean();
        if(bean.getClass().isAnnotationPresent(GoldAnnotation.class)){
            return true;
        }
        if(handler1.getMethod().isAnnotationPresent(GoldAnnotation.class)){
            return true;
        }
        return false;
    }
}
