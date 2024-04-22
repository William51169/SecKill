package com.javaproject.seckill.vo;

import com.javaproject.seckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 接收用户登录时，发送的信息（mobile, password)
 */
@Data
public class LoginVo {
    // 对LoginVo的属性值进行相应的约束
    @NotNull
    @IsMobile
    private String mobile;
    @NotNull
    @Length(min = 32)
    private String password;
}
