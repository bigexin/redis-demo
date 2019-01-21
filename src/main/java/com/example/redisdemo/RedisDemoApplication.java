package com.example.redisdemo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableCaching
@RestController
public class RedisDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisDemoApplication.class, args);
    }

    @Bean
    public KeyGenerator wiselyKeyGenerator() {
        return (Object o, Method m, Object... objects) -> {
            StringBuilder builder = new StringBuilder();
            builder.append(o.getClass().getName());
            builder.append(m.getName());
            for (Object object : objects) {
                builder.append(object.toString());
            }
            return builder.toString();
        };
    }

    //Redis缓存默认ttl
    private static final Integer DEFAULT_TTL = 1800;

    //特定缓存的过期时间策略
    private static Map<String, Integer> VALUE_MAP = new HashMap<>();

    static {
        VALUE_MAP.put("findUserInfoByUserNo", 10);
        VALUE_MAP.put("findUserStockByUserNo", 15);
    }

    /**
     * 操作redis的模板
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = getJackson2JsonRedisSerializer();
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 缓存管理，用于设置缓存ttl（过期时间）
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return new RedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                //默认过期时间
                this.getRedisCacheConfigurationWithTtl(DEFAULT_TTL),
                //读取配置
                this.getRedisCacheConfigurationMap());
    }

    private Jackson2JsonRedisSerializer<Object> getJackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }

    private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> map = new HashMap<>();
        //对特定value的缓存的策略
        VALUE_MAP.forEach((s, i) -> map.put(s, this.getRedisCacheConfigurationWithTtl(i)));
        return map;
    }

    private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Integer seconds) {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = getJackson2JsonRedisSerializer();

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer)
        ).entryTtl(Duration.ofSeconds(seconds));

        return redisCacheConfiguration;
    }

//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private Service service;

//    @GetMapping("/get")
//    public String get() {
//        return redisTemplate.opsForValue().get("test");
//    }
//
//    @GetMapping("/set")
//    public void set() {
//        redisTemplate.opsForValue().set("test", "value");
//    }

    @GetMapping("/getUser/{userNo}")
    public UserInfo getUser(@PathVariable int userNo) {
        return service.getUser(userNo);
    }

}

