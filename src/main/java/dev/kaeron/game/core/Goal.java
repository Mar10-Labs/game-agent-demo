package dev.kaeron.game.core;

public record Goal(int priority, String name, String description) {

    public String formatForPrompt() {
        return "## Goal %d: %s\n%s".formatted(priority, name, description);
    }
}
