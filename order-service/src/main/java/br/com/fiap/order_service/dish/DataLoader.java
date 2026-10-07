package br.com.fiap.order_service.dish;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final DishRepository dishRepository;

    @Override
    public void run(String... args) {
        if (dishRepository.count() > 0) return;

        dishRepository.saveAll(List.of(
                new Dish(null, "House Burger", "Brioche bun, beef patty and cheddar (flash promotion)", new BigDecimal("39.90"), 10),
                new Dish(null, "Margherita Pizza", "Tomato, mozzarella and basil (vegetarian)", new BigDecimal("44.90"), 20),
                new Dish(null, "Veggie Bowl", "Quinoa, roasted vegetables and tahini (vegetarian)", new BigDecimal("32.50"), 15),
                new Dish(null, "Vegan Burger", "Plant-based patty, tomato and lettuce (vegan)", new BigDecimal("36.90"), 12),
                new Dish(null, "Caesar Salad", "Romaine, grilled chicken and parmesan", new BigDecimal("29.90"), 15),
                new Dish(null, "Chocolate Brownie", "Warm brownie with vanilla ice cream (vegetarian)", new BigDecimal("18.50"), 25)
        ));
    }

}