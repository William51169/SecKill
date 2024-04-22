package com.javaproject.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 消息的发送者/生产者
 */
@Service
@Slf4j
public class MQSender {

    // 装配RabbitTemplate ——》 操作RabbitMQ
    @Resource
    private RabbitTemplate rabbitTemplate;

    // 方法：发送消息
    public void send(Object msg){
        log.info("send message-->" + msg);
        rabbitTemplate.convertAndSend("queue", msg);
    }

    // ----- fanout -----
    // 方法：发送消息到交换机
    public void sendFanout(Object msg){
        log.info("发送消息-->" + msg);
        // 第二个位置是路由，fanout模式要求忽略路由
        rabbitTemplate.convertAndSend("fanoutExchange", "", msg);
    }

    // ----- direct -----
    // 方法：发送消息到direct交换机，同时指定路由queue.red
    public void sendDirect1(Object msg){
        log.info("发送消息-->" + msg);
       rabbitTemplate.convertAndSend("directExchange", "queue.red", msg);
    }

    // 方法：发送消息到direct交换机，同时指定路由queue.red
    public void sendDirect2(Object msg){
        log.info("发送消息-->" + msg);
        rabbitTemplate.convertAndSend("directExchange", "queue.green", msg);;

    }

    // ----- topic -----
    // 方法：发送消息到topic交换机，同时指定路由queue.red.message
    public void sendTopic3(Object msg){
        log.info("发送消息-->" + msg);
        rabbitTemplate.convertAndSend("topicExchange", "queue.red.message", msg);;
    }

    // 方法：发送消息到topic交换机，同时指定路由green.queue.green.message
    public void sendTopic4(Object msg){
        log.info("发送消息-->" + msg);
        rabbitTemplate.convertAndSend("topicExchange", "green.queue.green.message", msg);;
    }

    // ----- headers -----
    // 方法：发送消息到headers交换机，同时携带/指定 匹配的K-V
    public void sendHeader01(String msg) {
        log.info("发送消息-->" + msg);
        // 创建消息属性
        MessageProperties properties = new MessageProperties();
        properties.setHeader("color", "red");
        properties.setHeader("speed", "fast");

        // 创建Message对象【包含了发送的消息本身和属性】
        Message message = new Message(msg.getBytes(), properties);
        rabbitTemplate.convertAndSend("headersExchange", "", message);
    }

    public void sendHeader02(String msg) {
        log.info("发送消息-->" + msg);
        // 创建消息属性
        MessageProperties properties = new MessageProperties();
        properties.setHeader("color", "red");
        properties.setHeader("speed", "normal");

        // 创建Message对象【包含了发送的消息本身和属性】
        Message message = new Message(msg.getBytes(), properties);
        rabbitTemplate.convertAndSend("headersExchange", "", message);
    }
}
