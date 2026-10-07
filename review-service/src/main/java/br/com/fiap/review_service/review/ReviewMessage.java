package br.com.fiap.review_service.review;

public record ReviewMessage(Long dishId, String dishName, int rating, String comment) {
}