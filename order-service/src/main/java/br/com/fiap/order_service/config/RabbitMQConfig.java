package br.com.fiap.order_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "delivery.exchange";
    public static final String QUEUE = "reviews.queue";
    public static final String ROUTING_KEY = "reviews.new";

    @Bean
    public TopicExchange deliveryExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue reviewsQueue() {
        return new Queue(QUEUE, true); // durável
    }

    @Bean
    public Binding reviewsBinding() {
        return BindingBuilder.bind(reviewsQueue()).to(deliveryExchange()).with(ROUTING_KEY);
    }

    // mensagens em JSON (o Boot usa este bean no RabbitTemplate)
    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

}