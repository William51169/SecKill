package com.javaproject.seckill.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * 生成UUID的工具类
 */
public class UUIDUtil {

    public static String uuid() {
        // 默认下：生成的字符串形式 xxxx-yyyy-zzz-ddd
        // 不想要"_",用replace("-","")替换
        return UUID.randomUUID().toString().replace("-","");
    }

    /*
    @Test
    public void testUUID() {
        System.out.println(UUIDUtil.uuid());
    }
    */

}
