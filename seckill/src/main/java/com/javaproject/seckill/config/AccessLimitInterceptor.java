package com.javaproject.seckill.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaproject.seckill.pojo.User;
import com.javaproject.seckill.service.UserService;
import com.javaproject.seckill.util.CookieUtil;
import com.javaproject.seckill.vo.RespBean;
import com.javaproject.seckill.vo.RespBeanEnum;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.method.HandlerMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;


/**
 * 自定义拦截器
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    // 装配需要的组件/对象
    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    // 1.得到User对象，放入ThreadLocal
    // 2.去处理@AccessLimit
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            // 获取登录的User对象
            User user = getUser(request, response);
            // 存入到ThreadLocal
            UserContext.setUser(user);

            // 把handler转成HandlerMethod
            HandlerMethod hm = (HandlerMethod) handler;
            // 获取目标方法的注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            // 如果目标方法没有@AccessLimit注解，说明该接口没有处理限流防刷
            if (accessLimit == null) {
                return true;
            }

            // 获取注解的值
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            // 说明用户必须登录才能访问目标方法
            if (needLogin) {
                if (user == null) {
                    // 返回一个用户信息错误的提示
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
            }
            String uri = request.getRequestURI();
            String key = uri + ":" + user.getId();
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer) valueOperations.get(key);
            if (count == null) {
                valueOperations.set(key, 1, second, TimeUnit.SECONDS);
            } else if (count < maxCount) { // 说明是正常访问
                valueOperations.increment(key);
            } else { // 此时，用户在频繁访问
                // 返回一个用户频繁访问的提示
                render(response, RespBeanEnum.ACCESS_LIMIT_REACHED);
                return false;
            }
        }


        return true;
    }

    // 方法：构建返回对象——以流的形式返回
    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        // 构建RespBean
        RespBean error = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(error));
        out.flush();
        out.close();

    }


    // 单独编写方法，得到登录对象 —— userTicket
    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        // 说明该用户没有登录
        if (!StringUtils.hasText(ticket)) {
            return null;
        }

        return userService.getUserByCookie(ticket, request, response);
    }
}
