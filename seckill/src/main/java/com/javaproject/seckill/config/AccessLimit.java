package com.javaproject.seckill.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {

    // 时间范围
    int second();
    // 访问最大次数
    int maxCount();
    // 是否登录
    boolean  needLogin() default true;
}
