package dev.kaeron.game.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.kaeron.game.repository.ArticleRepository;
import dev.kaeron.game.repository.InsightStore;
import dev.kaeron.game.core.Tool;
import dev.kaeron.game.core.ToolRegistry;

import java.util.Map;

public class NewsToolsRegistrar {

    private final ArticleRepository articleRepository;
    private final InsightStore insightStore;
    private final ObjectMapper mapper;

    public NewsToolsRegistrar(ArticleRepository articleRepository, InsightStore insightStore) {
        this.articleRepository = articleRepository;
        this.insightStore = insightStore;
        this.mapper = new ObjectMapper();
    }

    public void registerAll(ToolRegistry registry) {
        registerFetchHeadlines(registry);
        registerReadArticle(registry);
        registerCategorizeInsight(registry);
        registerTerminate(registry);
    }

    private void registerFetchHeadlines(ToolRegistry registry) {
        Tool tool = new Tool(
                "fetchHeadlines",
                "Returns today's tech headlines as JSON array with id, title, source, and category.",
                Map.of(),
                false
        );

        registry.register(tool, args -> {
            try {
                ArrayNode array = mapper.createArrayNode();
                for (var article : articleRepository.findAll()) {
                    ObjectNode node = mapper.createObjectNode();
                    node.put("id",       article.id());
                    node.put("title",    article.title());
                    node.put("source",   article.source());
                    node.put("category", article.category());
                    array.add(node);
                }
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(array);
            } catch (Exception e) {
                return "Error fetching headlines: " + e.getMessage();
            }
        });
    }

    private void registerReadArticle(ToolRegistry registry) {
        Tool tool = new Tool(
                "readArticle",
                "Reads the full content of an article given its id.",
                Map.of("articleId", "string — the id returned by fetchHeadlines"),
                false
        );

        registry.register(tool, args -> {
            String id = String.valueOf(args.get("articleId"));
            return articleRepository.findById(id)
                    .map(a -> """
                            Title: %s
                            Source: %s
                            Category: %s
                            Content: %s
                            """.formatted(a.title(), a.source(), a.category(), a.body()))
                    .orElse("Article not found with id=" + id);
        });
    }

    private void registerCategorizeInsight(ToolRegistry registry) {
        Tool tool = new Tool(
                "categorizeInsight",
                "Stores an insight extracted from an article. Call once per article to build the report progressively.",
                Map.of(
                        "category",       "string — e.g: JVM, AI Tools, Databases, Performance, AI Models",
                        "insight",        "string — concise takeaway relevant for a backend engineer",
                        "relevanceScore", "number 1-10 — how relevant for a senior backend engineer"
                ),
                false
        );

        registry.register(tool, args -> {
            insightStore.save(args);
            return "✓ Insight saved [category=%s | score=%s]"
                    .formatted(args.get("category"), args.get("relevanceScore"));
        });
    }

    private void registerTerminate(ToolRegistry registry) {
        Tool tool = new Tool(
                "terminate",
                "Ends the session and delivers the final report in markdown to the user.",
                Map.of("report", "string — complete markdown report grouping insights by category"),
                true
        );

        registry.register(tool, args -> {
            String report = String.valueOf(args.get("report"));
            System.out.println();
            System.out.println("═".repeat(60));
            System.out.println("📰  TECH NEWS REPORT — Backend Engineer Edition");
            System.out.println("═".repeat(60));
            System.out.println(report);
            System.out.println("═".repeat(60));
            return "Report delivered successfully.";
        });
    }
}
