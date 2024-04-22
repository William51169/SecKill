package com.javaproject.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQHeadersConfig {

        // ----- header -----
        // 定义队列名和交换机名
        private static final String QUEUE01 = "queue_header01";
        private static final String QUEUE02 = "queue_header02";
        private static final String EXCHANGE = "headersExchange";

        // 创建/配置队列和交换机
        @Bean
        public Queue queue_header01() {
            return new Queue(QUEUE01);
        }

        @Bean
        public Queue queue_header02() {
            return new Queue(QUEUE02);
        }

        @Bean
        public HeadersExchange headersExchange() {
            return new HeadersExchange(EXCHANGE);
        }

        // 完成队列和交换机的绑定，同时声明要匹配的k-v，和以什么方式来匹配（all/any)
        @Bean
        public Binding binding_header01() {
            // 先定义，声明K-V，因为可以有多个，所以将其放入map
            Map<String, Object> map = new HashMap<>();
            map.put("color", "red");
            map.put("speed", "low");
            return BindingBuilder.bind(queue_header01()).to(headersExchange()).whereAny(map).match();
        }

        @Bean
        public Binding binding_header02() {
            // 先定义，声明K-V，因为可以有多个，所以将其放入map
            Map<String, Object> map = new HashMap<>();
            map.put("color", "red");
            map.put("speed", "fast");
            return BindingBuilder.bind(queue_header02()).to(headersExchange()).whereAll(map).match();
        }
}
