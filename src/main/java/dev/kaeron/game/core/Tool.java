package dev.kaeron.game.core;

import java.util.Map;

public record Tool(
        String name,
        String description,
        Map<String, String> parameters,
        boolean terminal
) {

    public String formatForPrompt() {
        var sb = new StringBuilder();
        sb.append("### ").append(name).append("\n");
        sb.append(description).append("\n");

        if (!parameters.isEmpty()) {
            sb.append("Parameters:\n");
            parameters.forEach((k, v) ->
                    sb.append("  - ").append(k).append(": ").append(v).append("\n"));
        } else {
            sb.append("Parameters: none\n");
        }

        sb.append("Terminal: ").append(terminal).append("\n");
        return sb.toString();
    }
}
