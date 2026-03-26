package dev.kaeron.game.agent;

import dev.kaeron.game.core.*;
import java.util.List;

public class GameAgent {

    private static final long LLM_DELAY_MS = 1500;

    private final List<Goal> goals;
    private final ToolRegistry registry;
    private final Environment environment;
    private final GroqClient llm;
    private final PromptBuilder promptBuilder;
    private final ActionParser parser;

    public GameAgent(List<Goal> goals, ToolRegistry registry, GroqClient llm) {
        this.goals         = goals;
        this.registry      = registry;
        this.environment   = new Environment(registry);
        this.llm           = llm;
        this.promptBuilder = new PromptBuilder();
        this.parser        = new ActionParser(registry);
    }

    public Memory run(String userTask, int maxIterations) {
        Memory memory = new Memory();
        memory.add("user", userTask);

        printBanner(userTask);

        for (int i = 0; i < maxIterations; i++) {
            printIterationHeader(i + 1, maxIterations);

            String llmResponse = askLlmWithRetry(memory);

            if (llmResponse == null) {
                System.out.println("│ ❌ Error: Could not get response from LLM after retries.");
                System.out.println("│    Check your GROQ_API_KEY and internet connection.");
                break;
            }

            AgentAction action = parser.parse(llmResponse);
            System.out.printf("│ 🔧 Tool chosen: %s%n", action.toolName());
            System.out.printf("│    Args: %s%n", action.args());

            String result = environment.execute(action);
            System.out.println("│ ✅ Result: " + truncate(result, 300));
            System.out.println("└" + "─".repeat(56));

            memory.add("assistant", llmResponse);
            memory.add("user", "Tool result:\n" + result);

            if (action.isTerminal()) {
                System.out.printf("%n🏁 Agent finished in %d iterations.%n", i + 1);
                break;
            }

            sleep(LLM_DELAY_MS);
        }

        return memory;
    }

    private String askLlmWithRetry(Memory memory) {
        int maxRetries = 3;
        int retryDelay = 3000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                System.out.println("│ ⏳ Consulting Groq LLM...");
                var messages = promptBuilder.build(goals, registry.allTools(), memory);
                String response = llm.chat(messages);
                System.out.println("│ 🧠 LLM Response:\n" + indent(response, "│    "));
                return response;
            } catch (Exception e) {
                System.out.printf("│ ⚠️  LLM error (attempt %d/%d): %s%n", attempt, maxRetries, e.getMessage());
                if (attempt < maxRetries) {
                    System.out.printf("│    Retrying in %d seconds...%n", retryDelay / 1000);
                    sleep(retryDelay);
                    retryDelay *= 2;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void printBanner(String task) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║       GAME Framework — Tech News Analyzer Agent         ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.printf("║  G — Goals:       3 objectives defined                  ║%n");
        System.out.printf("║  A — Actions:     fetchHeadlines · readArticle ·         ║%n");
        System.out.printf("║                   categorizeInsight · terminate           ║%n");
        System.out.printf("║  M — Memory:      complete session history                ║%n");
        System.out.printf("║  E — Environment: local dataset (swappable)              ║%n");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.printf("║  Task: %-49s║%n", truncate(task, 49));
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private void printIterationHeader(int current, int max) {
        System.out.printf("%n┌─ Iteration %d/%d %s%n", current, max, "─".repeat(40));
    }

    private String indent(String text, String prefix) {
        if (text == null) return "";
        return text.lines()
                   .map(l -> prefix + l)
                   .reduce("", (a, b) -> a + "\n" + b);
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }
}
