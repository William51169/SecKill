package com.javaproject.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.javaproject.seckill.mapper.GoodsMapper;
import com.javaproject.seckill.mapper.OrderMapper;
import com.javaproject.seckill.pojo.*;
import com.javaproject.seckill.service.GoodsService;
import com.javaproject.seckill.service.OrderService;
import com.javaproject.seckill.service.SeckillGoodsService;
import com.javaproject.seckill.service.SeckillOrderService;
import com.javaproject.seckill.util.MD5Util;
import com.javaproject.seckill.vo.GoodsVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.javaproject.seckill.util.UUIDUtil;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    // 装配需要的组件/对象
    @Resource
    private SeckillGoodsService seckillGoodsService;

    // 注入组件
    @Resource
    private OrderMapper orderMapper;

    // 注入接口
    @Resource
    private SeckillOrderService seckillOrderService;

    //
    @Resource
    private RedisTemplate redisTemplate;

    // 完成秒杀
    @Override
    public Order seckill(User user, GoodsVo goodsVo) {
        // 查询秒杀商品的库存，并减一
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsVo.getId()));

        // 完成一个基本的秒杀操作[这块不具有原子性]，后面再高并发的情况下，还会优化
//        seckillGoods.setStockCount(seckillGoods.getStockCount()- 1);
//        seckillGoodsService.updateById(seckillGoods);

        // 老师分析：
        // 1. MySql 在默认的事务隔离级别[REPEATABLE READ]下
        // 2. 执行update语句时，会在事务中锁定要更新的行
        // 3. 这样可以防止其他会话在同一行执行update/delete语句

        // 只有在更新成功时，
        // 才会返回true，否则返回false，即更新后，受影响的行数 > 1 为 True
        boolean update = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count - 1").eq("goods_id", goodsVo.getId()).gt("stock_count", 0));

        // 如果更新失败，说明已经没有库存了
        if (!update) {
            return null;
        }

        // 生成普通订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L); // 设置一个初始值
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0); // 设置初始值，未支付的状态
        order.setCreateDate(new Date());

        orderMapper.insert(order);


        // 生成秒杀商品订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goodsVo.getId());
        // 这里秒杀商品订单对应的order_id，是从上面添加order后得到的
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setUserId(user.getId());

        // 保存seckillOrder
        seckillOrderService.save(seckillOrder);

        // 将生成的秒杀订单，存入到redis，这样在查询某个用户是否已经秒杀这个商品时
        // 直接到redis中查询，起到优化作用
        // 设计秒杀订单的key =》 order:用户id:商品id
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goodsVo.getId(), seckillOrder);

        return order;
    }

    // 方法：生成秒杀路径（唯一）
    @Override
    public String createPath(User user, Long goodsId) {
        // 生成秒杀路径
        String path = MD5Util.md5(UUIDUtil.uuid());
        // 将随机生成的路径，保存到Redis中，设置一个超时时间
        // key的设计：seckillPath:userId:goodsId
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, path, 60, TimeUnit.SECONDS);
        return path;
    }

    // 校验秒杀路径
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || goodsId < 0 || !StringUtils.hasText(path)) {
            return false;
        }

        // 取出该用户秒杀该商品的路径
        String redisPath = (String)redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }

    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (user== null || goodsId < 0 || !StringUtils.hasText(captcha)) {
            return false;
        }

        // 从redis中取出验证码
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
}
