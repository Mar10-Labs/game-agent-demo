package dev.kaeron.game.core;

public class Environment {

    private final ToolRegistry registry;

    public Environment(ToolRegistry registry) {
        this.registry = registry;
    }

    public String execute(AgentAction action) {
        try {
            Object result = registry.execute(action.toolName(), action.args());
            return result != null ? result.toString() : "(no result)";
        } catch (Exception e) {
            return "ERROR [%s]: %s".formatted(action.toolName(), e.getMessage());
        }
    }
}
