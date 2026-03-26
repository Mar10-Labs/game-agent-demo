package dev.kaeron.game.repository;

import dev.kaeron.game.model.Article;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository {
    List<Article> findAll();
    Optional<Article> findById(String id);
}
