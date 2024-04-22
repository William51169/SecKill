package com.javaproject.seckill.config;

import com.javaproject.seckill.pojo.User;

public class UserContext {

    // 每个线程都有自己的ThreadLocal，把共享的数据存放到这里，保证线程安全
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void setUser(User user){
        userHolder.set(user);
    }

    public static User getUser(){
        return userHolder.get();
    }
}
