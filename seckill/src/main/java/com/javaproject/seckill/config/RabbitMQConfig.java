package com.javaproject.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，可以创建队列、交换机……
 */

@Configuration
public class RabbitMQConfig {

    // 定义队列名
    private static final String QUEUE = "queue";

    // -- fanout--
    private static final String QUEUE1 = "queue_fanout01";
    private static final String QUEUE2 = "queue_fanout02";
    private static final String EXCHANGE = "fanoutExchange";

    // -- direct --
    private static final String QUEUE_DIRECT1 = "queue_direct01";
    private static final String QUEUE_DIRECT2 = "queue_direct02";
    private static final String EXCHANGE_DIRECT = "directExchange";

    // 定义路由
    private static final String ROUTING_KEY01 = "queue.red";
    private static final String ROUTING_KEY02 = "queue.green";

    // --- topic ---


    // 创建队列

    /**
     * 1. 配置队列
     * 2. 队列名 QUEUE(queue)
     * 3. true: 表示持久化
     * 队列在默认情况下放到内存中，rabbitmq重启后就丢失了，
     * 如果希望重启后，队列数据还能使用，就需要持久化(durable)
     * Erlang自带Mnesia数据库，当Rabbitmq重启后，会读取该数据库
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    /**
     * 创建队列，默认持久化
     * QUEUE1 = queue_fanout01
     */
    @Bean
    public Queue queue1() {
        return new Queue(QUEUE1);
    }

    // ----- fanout -----
    /**
     * 创建队列，默认持久化
     * QUEUE2 = queue_fanout02
     */
    @Bean
    public Queue queue2() {
        return new Queue(QUEUE2);
    }

    /**
     * 创建交换机
     * EXCHANGE = fanoutExchange
     */
    @Bean
    public FanoutExchange exchange() {
        return new FanoutExchange(EXCHANGE);
    }

    /**
     * 将 QUEUE1 = queue_fanout01 绑定到交换机
     */
    @Bean
    public Binding binding01() {
        return BindingBuilder.bind(queue1()).to(exchange());
    }

    /**
     * 将 QUEUE2 = queue_fanout02 绑定到交换机
     */
    @Bean
    public Binding binding02() {
        return BindingBuilder.bind(queue2()).to(exchange());
    }

    // ----- direct -----

    /**
     * 创建/配置队列， QUEUE_DIRECT1 = queue_direct01
     * @return
     */
    @Bean
    public Queue queue_direct1() {
        return new Queue(QUEUE_DIRECT1);
    }

    /**
     * 创建/配置队列， QUEUE_DIRECT2 = queue_direct02
     * @return
     */
    @Bean
    public Queue queue_direct2() {
        return new Queue(QUEUE_DIRECT2);
    }

    /**
     * 创建交换机
     * EXCHANGE_DIRECT = queue_exchange
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_DIRECT);
    }

    /**
     * 将 QUEUE_DIRECT1 = queue_direct1 绑定到交换机 directExchange = EXCHANGE_DIRECT
     * 同时声明/关联路由 ROUTING_KEY01 = queue.red
     */
    @Bean
    public Binding binding_direct1() {
        return BindingBuilder.bind(queue_direct1()).to(directExchange()).with(ROUTING_KEY01);
    }

    /**
     * 将 QUEUE_DIRECT2 = queue_direct2 绑定到交换机 directExchange = EXCHANGE_DIRECT
     * 同时声明/关联路由 ROUTING_KEY02 = queue.green
     */
    @Bean
    public Binding binding_direct2() {
        return BindingBuilder.bind(queue_direct2()).to(directExchange()).with(ROUTING_KEY02);
    }
}
