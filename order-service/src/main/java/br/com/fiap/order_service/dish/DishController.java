package br.com.fiap.order_service.dish;

import br.com.fiap.order_service.common.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DishController {

    private final DishRepository dishRepository;

    @GetMapping("/dishes")
    public List<Dish> list() {
        return dishRepository.findAll();
    }

    @GetMapping("/dishes/{id}")
    public Dish get(@PathVariable Long id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dish not found"));
    }

}