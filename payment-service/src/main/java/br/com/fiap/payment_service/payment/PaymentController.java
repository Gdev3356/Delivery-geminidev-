package br.com.fiap.payment_service.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

@Slf4j
@RestController
public class PaymentController {

    private final Random random = new Random();

    // com --server.port=8082 este valor muda automaticamente
    @Value("${server.port}")
    private int port;

    public record PaymentRequest(BigDecimal amount) {}
    public record PaymentResponse(String status, int instance) {}

    @PostMapping("/payments")
    public ResponseEntity<?> pay(@RequestBody PaymentRequest request) {
        if (random.nextBoolean()) { // ~50% de falha
            log.warn("[instance {}] simulated payment failure (amount={})", port, request.amount());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Simulated payment failure", "instance", port));
        }

        log.info("[instance {}] payment approved (amount={})", port, request.amount());
        return ResponseEntity.ok(new PaymentResponse("APPROVED", port));
    }

}