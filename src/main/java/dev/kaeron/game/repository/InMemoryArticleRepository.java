package dev.kaeron.game.repository;

import dev.kaeron.game.config.AppConfig;
import dev.kaeron.game.model.Article;

import java.util.List;
import java.util.Optional;

public class InMemoryArticleRepository implements ArticleRepository {

    private final List<Article> articles;

    public InMemoryArticleRepository(AppConfig config) {
        this.articles = loadFromConfig(config);
    }

    @Override
    public List<Article> findAll() {
        return articles;
    }

    @Override
    public Optional<Article> findById(String id) {
        return articles.stream()
                .filter(a -> a.id().equals(id))
                .findFirst();
    }

    private List<Article> loadFromConfig(AppConfig config) {
        int count = config.getInt("ARTICLE_COUNT", 0);

        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> buildArticle(config, i))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Optional<Article> buildArticle(AppConfig config, int index) {
        String prefix = "ARTICLE_" + index + "_";
        String id       = config.get(prefix + "ID");
        String title    = config.get(prefix + "TITLE");
        String source   = config.get(prefix + "SOURCE");
        String category = config.get(prefix + "CATEGORY");
        String body     = config.get(prefix + "BODY");

        if (id == null || title == null || source == null || category == null || body == null) {
            System.out.printf("⚠️  Article %d incomplete in .env — skipped%n", index);
            return Optional.empty();
        }

        return Optional.of(new Article(id, title, source, category, body));
    }
}
