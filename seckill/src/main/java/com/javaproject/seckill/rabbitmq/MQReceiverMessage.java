package com.javaproject.seckill.rabbitmq;

import cn.hutool.json.JSONUtil;
import com.javaproject.seckill.pojo.Order;
import com.javaproject.seckill.pojo.SeckillMessage;
import com.javaproject.seckill.pojo.User;
import com.javaproject.seckill.service.GoodsService;
import com.javaproject.seckill.service.OrderService;
import com.javaproject.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 消息的接收者/消费者
 */
@Service
@Slf4j
public class MQReceiverMessage {

    // 装配需要的组件
    @Resource
    private GoodsService goodsService;

    @Resource
    private OrderService orderService;

    // 接收消息，并完成下单
    @RabbitListener(queues = "seckillQueue")
    public void queue(String message) {
        log.info("接收到的消息-->" + message);

        // 这里我们取出的是String
        // 但是我们需要的是SeckillMessage，因此，需要一个工具类 JSONUtil
        SeckillMessage seckillMessage = JSONUtil.toBean(message, SeckillMessage.class);

        // 秒杀用户对象
        User user = seckillMessage.getUser();
        // 秒杀商品id
        Long goodsId = seckillMessage.getGoodsId();
        // 通过商品id，得到对应的GoodsVo
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        // 下单操作
        orderService.seckill(user, goodsVo);
    }
}
