package dev.kaeron.game.core;

import java.util.Map;

public record AgentAction(Tool tool, Map<String, Object> args) {

    public String toolName()    { return tool.name(); }
    public boolean isTerminal() { return tool.terminal(); }
}
