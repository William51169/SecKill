package com.javaproject.seckill.util;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5加密工具类
 */
public class MD5Util {
    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    // 准备一个 salt [前端使用的盐]
    private static final  String SALT = "abcdefg";

    // 加密加盐方法，完成第一个加密加盐 md5（password明文 + salt1）
    public static String inputPassToMidPass(String inputPass) {
        String str = SALT.charAt(0) + inputPass + SALT.charAt(6);
        return md5(str);
    }

    // 加密加盐，完成的任务是把MidPassword(MidPass + salt2) 转成 db中的密码
    // （md5（md5（password明文 + salt1）+ salt2）
    public static String midPassToDBPass(String midPass, String salt) {
        String str = salt.charAt(1) + midPass + salt.charAt(5);
        return md5(str);
    }

    // 将password明文，直接转换成DB中的密码
    public static String inputPassToDBPass(String inputPass, String salt) {
        String midPass = inputPassToMidPass(inputPass);
        String dbPass = midPassToDBPass(midPass, salt);
        return dbPass;
    }
}
