package com.goldgyro.platform;

import com.goldgyro.platform.foreground.redisService.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class Test {

    @Autowired
    private RedisService redisService;
    static final int HASH_BITS = 0x7fffffff;

    @GetMapping("/set/{key}/{value}")
    public String set(@PathVariable String key, @PathVariable String value){
        if(redisService.set(key,value)){
            redisService.expire(key,10000);
            return "success";
        }
        return "failure";

    }
    @GetMapping("/get/{key}")
    public String get(@PathVariable String key){
        return redisService.get(key);
    }

    static final int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }

    public static void main(String[] args) {
        int i = 789654;
        System.out.println(spread(i));
        String s = "1111111111111111111111111111111";
        System.out.println(s.length());
    }

}
