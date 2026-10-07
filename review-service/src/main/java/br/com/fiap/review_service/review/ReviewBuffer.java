package br.com.fiap.review_service.review;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReviewBuffer {

    public static class Accumulator {
        private final String dishName;
        private long sum;
        private long count;

        Accumulator(String dishName) {
            this.dishName = dishName;
        }

        void add(int rating) {
            sum += rating;
            count++;
        }

        public String getDishName() { return dishName; }
        public long getSum() { return sum; }
        public long getCount() { return count; }
    }

    private final ConcurrentHashMap<Long, Accumulator> buffer = new ConcurrentHashMap<>();

    /** compute() é atômico por chave: seguro com vários consumidores. */
    public void add(ReviewMessage message) {
        buffer.compute(message.dishId(), (id, current) -> {
            var acc = current == null ? new Accumulator(message.dishName()) : current;
            acc.add(message.rating());
            return acc;
        });
    }

    public Set<Long> dishIds() {
        return buffer.keySet();
    }

    /** Retira o acumulado do buffer; novas avaliações começam um acumulador novo. */
    public Accumulator take(Long dishId) {
        return buffer.remove(dishId);
    }

}