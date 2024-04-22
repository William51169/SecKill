package com.javaproject.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.javaproject.seckill.pojo.User;
import com.javaproject.seckill.vo.LoginVo;
import com.javaproject.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService extends IService<User> {

    // 方法 完成用户的登录校验
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    // 根据Cookie值 - ticket 获取用户
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    // 方法：更新密码
    RespBean updatePassword(String userTicket, String password,HttpServletRequest request, HttpServletResponse response);
}
