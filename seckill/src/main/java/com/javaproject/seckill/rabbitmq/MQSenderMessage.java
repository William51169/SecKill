package com.javaproject.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 消息的生产者/发送者，主要发送秒杀消息
 */
@Service
@Slf4j
public class MQSenderMessage {

    // 装配RabbitTemplate
    @Resource
    private RabbitTemplate rabbitTemplate;

    // 方法：发送消息
    public void sendSeckillMessage(String message) {
        log.info("发送消息-->" + message);
        rabbitTemplate.convertAndSend("seckillExchange", "seckill.message", message);
    }
}
