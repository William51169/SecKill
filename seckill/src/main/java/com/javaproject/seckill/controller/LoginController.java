package com.javaproject.seckill.controller;

import com.javaproject.seckill.service.UserService;
import com.javaproject.seckill.vo.LoginVo;
import com.javaproject.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    // 装配 UserService
    @Resource
    private UserService userService;
    // 编写方法，可以进入到登陆界面
    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

    // 编写方法，处理用户登录请求
    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        // 观察数据
        // log.info("{}", loginVo);
        return userService.doLogin(loginVo, request, response);
    }
}
