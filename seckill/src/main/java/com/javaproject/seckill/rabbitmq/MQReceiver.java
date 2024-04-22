package com.javaproject.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 消息的接收者
 */
@Service
@Slf4j
public class MQReceiver {

    // 方法：接收消息
    @RabbitListener(queues = "queue")
    public void receive(Object msg) {
        log.info("接收到消息-->" + msg);
    }

    // ----- fanout -----
    // 方法：监听队列queue_fanout01
    @RabbitListener(queues = "queue_fanout01")
    public void receive1(Object msg) {
        log.info("从queue_fanout01接收到消息-->" + msg);
    }

    // 方法：监听队列queue_fanout02
    @RabbitListener(queues = "queue_fanout02")
    public void receive2(Object msg) {
        log.info("从queue_fanout02收到消息-->" + msg);
    }

    // ----- direct -----
    // 方法：监听队列queue_direct01
    @RabbitListener(queues = "queue_direct01")
    public void queue_direct1(Object msg) {
        log.info("从queue_direct01收到消息-->" + msg);
    }

    // 方法：监听队列queue_direct02
    @RabbitListener(queues = "queue_direct02")
    public void queue_direct2(Object msg) {
        log.info("从queue_direct02收到消息-->" + msg);
    }

    // ----- topic -----
    // 方法：监听队列queue_topic01
    @RabbitListener(queues = "queue_topic01")
    public void queue_topic1(Object msg) {
        log.info("从queue_topic01收到消息-->" + msg);
    }

    // 方法：监听队列queue_topic02
    @RabbitListener(queues = "queue_topic02")
    public void queue_topic2(Object msg) {
        log.info("从queue_topic02收到消息-->" + msg);
    }

    // ----- headers -----
    // 方法：监听队列queue_header01
    @RabbitListener(queues = "queue_header01")
    public void queue_header01(Message massage) {
        log.info("从queue_header01接收到消息对象-->" + massage);
        log.info("从queue_header01接收到消息内容-->" + new String(massage.getBody()));
    }

    // 方法：监听队列queue_header02
    @RabbitListener(queues = "queue_header02")
    public void queue_header02(Message massage) {
        log.info("从queue_header02接收到消息对象-->" + massage);
        log.info("从queue_header02接收到消息内容-->" + new String(massage.getBody()));
    }
}
