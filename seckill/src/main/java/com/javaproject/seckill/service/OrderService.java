package com.javaproject.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.javaproject.seckill.pojo.Goods;
import com.javaproject.seckill.pojo.Order;
import com.javaproject.seckill.pojo.User;
import com.javaproject.seckill.vo.GoodsVo;

public interface OrderService extends IService<Order> {
    // 方法：秒杀
    Order seckill(User user, GoodsVo goodsVo);

    // 方法：生成秒杀路径（唯一）
    String createPath(User user, Long goodsId);

    // 方法：校验秒杀路径
    boolean checkPath(User user, Long goodsId, String path);

    // 方法：验证用户输入的验证码是否正确
    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
