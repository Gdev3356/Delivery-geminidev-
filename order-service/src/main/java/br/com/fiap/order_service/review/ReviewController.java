package br.com.fiap.order_service.review;

import br.com.fiap.order_service.common.ApiException;
import br.com.fiap.order_service.dish.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static br.com.fiap.order_service.config.RabbitMQConfig.EXCHANGE;
import static br.com.fiap.order_service.config.RabbitMQConfig.ROUTING_KEY;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final DishRepository dishRepository;
    private final RabbitTemplate rabbitTemplate;

    public record ReviewRequest(Long dishId, Integer rating, String comment) {}

    @PostMapping("/reviews")
    public ResponseEntity<Void> create(@RequestBody ReviewRequest request) {
        if (request == null || request.dishId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "dishId is required");
        }
        if (request.rating() == null || request.rating() < 1 || request.rating() > 5) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
        }

        var dish = dishRepository.findById(request.dishId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dish not found"));

        var message = new ReviewMessage(
                dish.getId(),
                dish.getName(),
                request.rating(),
                request.comment() == null ? "" : request.comment()
        );

        // não grava no banco: só publica na fila
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, message);

        return ResponseEntity.accepted().build(); // 202 sem corpo
    }

}