package com.goldgyro.platform;

import com.goldgyro.platform.foreground.redisService.RedisService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 应用主程序
 * @author wg2993
 * @version v1.0 2018/7/8
 */
@SpringBootApplication
public class Main
{
	public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
