package com.javaproject.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.javaproject.seckill.exception.GlobalException;
import com.javaproject.seckill.mapper.UserMapper;
import com.javaproject.seckill.pojo.User;
import com.javaproject.seckill.service.UserService;
import com.javaproject.seckill.util.CookieUtil;
import com.javaproject.seckill.util.MD5Util;
import com.javaproject.seckill.util.UUIDUtil;
import com.javaproject.seckill.util.ValidatorUtil;
import com.javaproject.seckill.vo.LoginVo;
import com.javaproject.seckill.vo.RespBean;
import com.javaproject.seckill.vo.RespBeanEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Resource
    private UserMapper userMapper;

    // 配置RedisTemplate，操作Redis
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        //接收到mobile和password[midPass]
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

//        // 判断手机号/id 和密码是否为空
//        if (!StringUtils.hasText(mobile) || !StringUtils.hasText(password)) {
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//       }
//
//        // 校验手机号码是否合格
//        if (!ValidatorUtil.isMobile(mobile)) {
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }

        // 查询DB，看看用户是否存在
        User user = userMapper.selectById(mobile);
        if (user == null) {
            // return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        // 如果用户存在，则比对密码
        // 注意，我们从 logVo 取出的密码是中间密码（即客户端经过一次加密加盐处理的密码）
        if (!MD5Util.midPassToDBPass(password, user.getSalt()).equals(user.getPassword())) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

        // 登录成功
        // 为每个用户生成票据，唯一的
        String ticket = UUIDUtil.uuid();
        // 将登录成功的用户保存到session
//        request.getSession().setAttribute(ticket, user);

        // 为了实现分布式Session，把登录的用户存放到Redis
        System.out.println("使用的 redisTemplate:" + redisTemplate.hashCode());
        redisTemplate.opsForValue().set("user:" + ticket, user);
        // 将ticket保存到cookie
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        // 返回ticket
        return RespBean.success(ticket);
    }

    // 根据Cookie-ticket获取用户
    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (!StringUtils.hasText(userTicket)) {
            return null;
        }

        // 根据userTicket到Redis获取user
        User user = (User)redisTemplate.opsForValue().get("user:" + userTicket);
        // 如果用户不为null，就需要重新设置cookie，刷新，这里是根据你的业务需要来的
        if (user != null) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }

        return user;
    }

    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        if (user == null) {
            // 抛出异常
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }

        // 明文密码, 设置新密码
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        int i = userMapper.updateById(user);

        // 更新成功
        if (i == 1) {
            // 删除该用户在Redis中的数据
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }

        // 更新失败
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }
}
