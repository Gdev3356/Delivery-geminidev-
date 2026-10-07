package br.com.fiap.review_service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewFlushJob {

    private final ReviewBuffer buffer;
    private final ReviewSummaryRepository repository;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void flush() {
        int flushed = 0;

        for (Long dishId : buffer.dishIds()) {
            var acc = buffer.take(dishId); // remove atômico: nenhuma avaliação se perde
            if (acc == null) continue;

            var summary = repository.findById(dishId)
                    .orElseGet(() -> new ReviewSummary(dishId, acc.getDishName()));
            summary.add(acc.getSum(), acc.getCount());
            repository.save(summary);
            flushed += (int) acc.getCount();
        }

        if (flushed > 0) {
            log.info("Flushed {} reviews to the database", flushed);
        }
    }

}