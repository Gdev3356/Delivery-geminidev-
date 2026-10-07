package br.com.fiap.order_service.review;

public record ReviewMessage(Long dishId, String dishName, int rating, String comment) {
}