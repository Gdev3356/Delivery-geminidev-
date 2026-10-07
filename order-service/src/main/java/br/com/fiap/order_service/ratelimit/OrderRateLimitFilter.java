package br.com.fiap.order_service.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/** Janela fixa de 1 segundo: no máximo 20 POST /orders por segundo. */
@Component
public class OrderRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_PER_SECOND = 20;

    private long currentSecond = 0;
    private int count = 0;

    private synchronized boolean tryAcquire() {
        long nowSecond = System.currentTimeMillis() / 1000;
        if (nowSecond != currentSecond) {
            currentSecond = nowSecond;
            count = 0;
        }
        return ++count <= MAX_PER_SECOND;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equals(request.getMethod()) && "/orders".equals(request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (!tryAcquire()) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many requests, try again in a second\"}");
            return;
        }
        chain.doFilter(request, response);
    }

}