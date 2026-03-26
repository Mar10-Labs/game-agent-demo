package dev.kaeron.game.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ToolRegistry {

    private final Map<String, Tool> tools = new HashMap<>();
    private final Map<String, Function<Map<String, Object>, Object>> bindings = new HashMap<>();

    public void register(Tool tool, Function<Map<String, Object>, Object> binding) {
        tools.put(tool.name(), tool);
        bindings.put(tool.name(), binding);
    }

    public Tool findTool(String name) {
        Tool tool = tools.get(name);
        if (tool == null) {
            throw new IllegalArgumentException("Unknown tool: '" + name + "'");
        }
        return tool;
    }

    public Object execute(String toolName, Map<String, Object> args) {
        Function<Map<String, Object>, Object> binding = bindings.get(toolName);
        if (binding == null) {
            throw new IllegalArgumentException("No binding for tool: '" + toolName + "'");
        }
        return binding.apply(args);
    }

    public List<Tool> allTools() {
        return new ArrayList<>(tools.values());
    }
}
