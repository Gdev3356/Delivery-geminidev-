package br.com.fiap.review_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // mesmo nome e mesma durabilidade declarados no order-service
    public static final String QUEUE = "reviews.queue";

    @Bean
    public Queue reviewsQueue() {
        return new Queue(QUEUE, true);
    }

    // o tipo é inferido pelo parâmetro do @RabbitListener,
    // então o ReviewMessage pode viver em pacotes diferentes nos dois serviços
    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

}