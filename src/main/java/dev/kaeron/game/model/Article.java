package dev.kaeron.game.model;

public record Article(
        String id,
        String title,
        String source,
        String category,
        String body
) {}
