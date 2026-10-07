package br.com.fiap.review_service.review;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ReviewSummary {

    @Id
    private Long dishId;

    private String dishName;

    private long totalRating;

    private long ratingCount;

    public ReviewSummary(Long dishId, String dishName) {
        this.dishId = dishId;
        this.dishName = dishName;
    }

    public void add(long sum, long count) {
        this.totalRating += sum;
        this.ratingCount += count;
    }

}