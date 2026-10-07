package br.com.fiap.order_service.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final RestTemplate restTemplate;

    public record PaymentRequest(BigDecimal amount) {}
    public record PaymentResponse(String status, Integer instance) {}

    @Retryable(
            includes = {HttpServerErrorException.class, ResourceAccessException.class},
            maxRetries = 5,
            delay = 200,
            multiplier = 2,   // backoff exponencial
            jitter = 100,
            maxDelay = 2000
    )
    public PaymentResponse charge(BigDecimal amount) {
        log.info("Calling PAYMENT-SERVICE (amount={})", amount);

        // nome registrado no Eureka: o @LoadBalanced escolhe a instância
        var response = restTemplate.postForObject(
                "http://PAYMENT-SERVICE/payments",
                new PaymentRequest(amount),
                PaymentResponse.class
        );

        log.info("Payment answered by instance {}: {}",
                response != null ? response.instance() : null,
                response != null ? response.status() : null);
        return response;
    }

}