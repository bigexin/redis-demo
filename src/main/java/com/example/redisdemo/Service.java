package com.example.redisdemo;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class Service {

    @Cacheable(keyGenerator = "wiselyKeyGenerator", value = "getUser")
    public UserInfo getUser(int userNo) {
        System.out.println("方法执行了");
        return new UserInfo(1, "xxx", "addr", "xx@xx.com");
    }

}
