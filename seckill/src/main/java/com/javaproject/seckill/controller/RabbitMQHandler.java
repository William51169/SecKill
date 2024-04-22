package com.javaproject.seckill.controller;

import com.javaproject.seckill.rabbitmq.MQSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class RabbitMQHandler {

    // 装配MQSender
    @Resource
    private MQSender mqSender;

    // 方法：调用消息生产者，发送消息
    @RequestMapping("/mq")
    @ResponseBody
    public void mq() {
        mqSender.send("hello, java~~");
    }

    // -----fanout---------
    // 方法：调用消息生产者，发送消息到交换机
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public void fanout() {
        mqSender.sendFanout("hello, 交换机~~");
    }

    // -----direct-----
    // 调用消息生产者，发送消息到交换机directExchange
    @RequestMapping("/mq/direct01")
    @ResponseBody
    public void driect01() {
        mqSender.sendDirect1("hello, smith~~");
    }

    // 调用消息生产者，发送消息到交换机directExchange
    @RequestMapping("/mq/direct02")
    @ResponseBody
    public void driect02() {
        mqSender.sendDirect2("hello, tomcat~~");
    }

    // ----- topic -----
    // 调用消息生产者，发送消息到交换机topicExchange
    @RequestMapping("/mq/topic01")
    @ResponseBody
    public void topic01() {
        mqSender.sendTopic3("hello, red~~");
    }

    // 调用消息生产者，发送消息到交换机topicExchange
    @RequestMapping("/mq/topic02")
    @ResponseBody
    public void topic02() {
        mqSender.sendTopic4("hello, green~~");
    }

    // ----- header -----
    // 调用消息生产者，发送消息到交换机headersExchange
    @RequestMapping("/mq/header01")
    @ResponseBody
    public void header01() {
        mqSender.sendHeader01("hello, ABC~~");
    }

    @RequestMapping("/mq/header02")
    @ResponseBody
    public void header02() {
        mqSender.sendHeader02("hello, xxx~~");
    }


}
