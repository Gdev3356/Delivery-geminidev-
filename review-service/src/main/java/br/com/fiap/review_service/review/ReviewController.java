package br.com.fiap.review_service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewSummaryRepository repository;

    public record RankingItem(Long dishId, String dishName, double average, long count) {}

    @GetMapping("/reviews/ranking")
    public List<RankingItem> ranking() {
        return repository.findAll().stream()
                .filter(s -> s.getRatingCount() > 0)
                .map(s -> new RankingItem(
                        s.getDishId(),
                        s.getDishName(),
                        Math.round((double) s.getTotalRating() / s.getRatingCount() * 100.0) / 100.0,
                        s.getRatingCount()))
                .sorted(Comparator.comparingDouble(RankingItem::average)
                        .thenComparingLong(RankingItem::count)
                        .reversed())
                .toList();
    }

}