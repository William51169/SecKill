package com.javaproject.seckill.util;

import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 完成一些校验工作，比如手机号码格式是否正确
 */
public class ValidatorUtil {

    // 校验手机号码的正则表达式
    private static final Pattern mobile_pattern = Pattern.compile("^1[3-9][0-9]{9}$");

    // 编写方法
    public static boolean isMobile(String mobile){
        if (!StringUtils.hasText(mobile)) {
            return false;
        }

        Matcher matcher = mobile_pattern.matcher(mobile);
        return matcher.matches();
    }

    // 测试一下校验方法
    @Test
    public void test1(){
        System.out.println(isMobile("10997574471"));
    }
}
