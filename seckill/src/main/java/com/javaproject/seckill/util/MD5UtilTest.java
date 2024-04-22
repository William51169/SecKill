package com.javaproject.seckill.util;

import org.junit.jupiter.api.Test;

/**
 * 测试MD5Util方法的使用
 */
public class MD5UtilTest {

    @Test
    public void testMD5() {
        // 密码明文 “12345”
        // 1. 获取密码明文 “12345” 的中间密码[即客户端加密加盐后]，在网络上传输的密码
        // 2. 即第一次加密加盐处理
        // 3. 这个加密加盐的工作会在客户端/浏览器完成
        // System.out.println(MD5Util.inputPassToMidPass("12345"));

        // 中间密码 “ba3cd05deb76b10870843aa07654840e”
        // 4. 对中间密码进行二次加密加盐，即后端得到的密码
        // 5. 即第二次加密加盐处理
        // 6. 这个加密加盐的工作会在服务端完成
        // 13a4ea7a48838c78a1b537aafc121308  hYLLSQ4x

        // GfBiQk1X
        // System.out.println(MD5Util.midPassToDBPass("13a4ea7a48838c78a1b537aafc121308", "GfBiQk1X"));

        // 7. 明文密码 ”12345“ ——> 得到存放到DB密码
        // 90aaa1d4eb39153a4eec426def4e5c96 hYLLSQ4x

        // cde0adcd6208d77b9105fa0d60c33c17 GfBiQk1X
        System.out.println(MD5Util.inputPassToDBPass("12345", "GfBiQk1X"));
    }
}
