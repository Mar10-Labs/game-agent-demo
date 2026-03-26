package dev.kaeron.game.agent;

import dev.kaeron.game.core.Goal;
import dev.kaeron.game.core.Memory;
import dev.kaeron.game.core.Tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromptBuilder {

    private static final String RESPONSE_FORMAT = """
            
            # Response Format
            Respond ONLY with this JSON block. No additional text before or after:
            ```action
            {
              "tool": "toolName",
              "args": { "parameter": "value" }
            }
            ```
            - If the tool has no parameters, use "args": {}
            - Choose exactly ONE tool per response
            - Do not add any text outside the ```action block
            """;

    public List<Map<String, String>> build(
            List<Goal> goals,
            List<Tool> tools,
            Memory memory
    ) {
        List<Map<String, String>> messages = new ArrayList<>();

        messages.add(msg("system", buildSystemPrompt(goals, tools)));

        for (Memory.Entry entry : memory.getRelevantMemories()) {
            messages.add(msg(entry.role(), entry.content()));
        }

        return messages;
    }

    private String buildSystemPrompt(List<Goal> goals, List<Tool> tools) {
        var sb = new StringBuilder();

        sb.append("# You are an AI agent. Your objectives are:\n\n");
        goals.stream()
                .sorted((a, b) -> Integer.compare(a.priority(), b.priority()))
                .forEach(g -> sb.append(g.formatForPrompt()).append("\n\n"));

        sb.append("# Available Tools\n\n");
        tools.forEach(t -> sb.append(t.formatForPrompt()).append("\n"));

        sb.append(RESPONSE_FORMAT);
        return sb.toString();
    }

    private static Map<String, String> msg(String role, String content) {
        Map<String, String> m = new HashMap<>();
        m.put("role", role);
        m.put("content", content);
        return m;
    }
}
