package dev.kaeron.game.demo;

import dev.kaeron.game.agent.GameAgent;
import dev.kaeron.game.agent.GroqClient;
import dev.kaeron.game.config.AppConfig;
import dev.kaeron.game.core.Goal;
import dev.kaeron.game.core.ToolRegistry;
import dev.kaeron.game.repository.ArticleRepository;
import dev.kaeron.game.repository.InMemoryArticleRepository;
import dev.kaeron.game.repository.InMemoryInsightStore;
import dev.kaeron.game.repository.InsightStore;
import dev.kaeron.game.tools.NewsToolsRegistrar;

import java.util.List;

public class NewsAnalyzerDemo {

    public static void main(String[] args) {

        AppConfig config = new AppConfig();

        ArticleRepository articleRepo = new InMemoryArticleRepository(config);
        InsightStore insightStore     = new InMemoryInsightStore();

        // ── A: Actions — tools with their implementations ───────────────────
        ToolRegistry registry = new ToolRegistry();
        new NewsToolsRegistrar(articleRepo, insightStore).registerAll(registry);

        // ── G: Goals — read from .env (with fallback to useful defaults) ─
        List<Goal> goals = List.of(
                new Goal(1, "Get Headlines",
                        config.getOrDefault("GOAL_1",
                                "ALWAYS start by calling fetchHeadlines to get the list of articles.")),

                new Goal(2, "Analyze Articles",
                        config.getOrDefault("GOAL_2",
                                """
                                For EACH article in the list:
                                  1. Call readArticle with its id to read the full content
                                  2. Call categorizeInsight with a concise takeaway and relevanceScore from 1 to 10
                                Process ALL articles before moving to step 3.
                                """)),

                new Goal(3, "Deliver Report",
                        config.getOrDefault("GOAL_3",
                                """
                                When you have processed ALL articles, call terminate with a
                                well-structured markdown report that includes:
                                  - Insights grouped by category
                                  - Section "⭐ Top Pick for Backend Engineers" with the most relevant item
                                  - One actionable recommendation per category
                                """))
        );

        // ── LLM — all config comes from AppConfig ───────────────────────
        GroqClient llm = new GroqClient(config);

        // ── Agent + Run ───────────────────────────────────────────────────
        String task       = config.getOrDefault("AGENT_TASK",
                "Analyze today's tech news and prepare a report for a senior backend engineer.");
        int maxIterations = config.getInt("AGENT_MAX_ITERATIONS", 15);

        new GameAgent(goals, registry, llm).run(task, maxIterations);
    }
}
