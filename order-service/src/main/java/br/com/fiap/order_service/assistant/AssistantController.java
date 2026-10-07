package br.com.fiap.order_service.assistant;

import br.com.fiap.order_service.common.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AssistantController {

    private final ChatService chatService;

    public record AssistantRequest(String question) {}
    public record AssistantResponse(String answer) {}

    @PostMapping("/assistant")
    public AssistantResponse ask(@RequestBody AssistantRequest request) {
        if (request == null || request.question() == null || request.question().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Question is required");
        }
        if (request.question().length() > 500) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Question is too long (max 500 characters)");
        }

        try {
            return new AssistantResponse(chatService.ask(request.question().trim()));
        } catch (Exception e) {
            log.error("Assistant failed", e);
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Assistant is unavailable right now");
        }
    }

}