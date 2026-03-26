package dev.kaeron.game.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArticleTest {

    @Test
    void record_shouldHaveCorrectValues() {
        Article article = new Article("1", "Test Title", "Source", "JVM", "Article body");

        assertEquals("1", article.id());
        assertEquals("Test Title", article.title());
        assertEquals("Source", article.source());
        assertEquals("JVM", article.category());
        assertEquals("Article body", article.body());
    }
}
