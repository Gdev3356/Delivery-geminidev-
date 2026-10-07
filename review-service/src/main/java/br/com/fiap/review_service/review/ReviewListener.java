package br.com.fiap.review_service.review;

import br.com.fiap.review_service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewListener {

    private final ReviewBuffer buffer;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void onReview(ReviewMessage message) {
        log.debug("Review received: dish={} rating={}", message.dishId(), message.rating());
        buffer.add(message); // só memória: nada de banco aqui
    }

}