package br.com.fiap.order_service.order;

import br.com.fiap.order_service.common.ApiException;
import br.com.fiap.order_service.dish.DishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final DishRepository dishRepository;
    private final CustomerOrderRepository orderRepository;
    private final PaymentClient paymentClient;

    public record OrderRequest(Long dishId, Integer quantity) {}

    /**
     * Tudo acontece na mesma transação, com a linha do prato travada.
     * Se o pagamento falhar depois dos retries, a ApiException (RuntimeException)
     * faz rollback: o estoque não é descontado e nenhum pedido é salvo.
     */
    @Transactional
    public CustomerOrder createOrder(OrderRequest request) {
        if (request == null || request.dishId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "dishId is required");
        }
        if (request.quantity() == null || request.quantity() < 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Quantity must be at least 1");
        }

        var dish = dishRepository.findByIdForUpdate(request.dishId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dish not found"));

        if (dish.getStock() < request.quantity()) {
            throw new ApiException(HttpStatus.CONFLICT, "Dish out of stock");
        }

        var total = dish.getPrice().multiply(BigDecimal.valueOf(request.quantity()));

        try {
            var payment = paymentClient.charge(total);
            if (payment == null || !"APPROVED".equals(payment.status())) {
                throw new IllegalStateException("Payment not approved");
            }
        } catch (Exception e) {
            log.error("Payment failed for dish {}: {}", dish.getId(), e.getMessage());
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Payment service unavailable, please try again");
        }

        dish.setStock(dish.getStock() - request.quantity());

        var order = new CustomerOrder(
                null,
                dish.getId(),
                request.quantity(),
                total,
                OrderStatus.CONFIRMED,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
        return orderRepository.save(order);
    }

}