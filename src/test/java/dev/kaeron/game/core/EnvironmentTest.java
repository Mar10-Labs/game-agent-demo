package dev.kaeron.game.core;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentTest {

    @Test
    void execute_shouldReturnToolResult() {
        ToolRegistry registry = new ToolRegistry();
        registry.register(
                new Tool("echo", "Echo", Map.of("msg", "string"), false),
                args -> "Result: " + args.get("msg")
        );

        Environment env = new Environment(registry);
        AgentAction action = new AgentAction(registry.findTool("echo"), Map.of("msg", "test"));

        String result = env.execute(action);
        assertEquals("Result: test", result);
    }

    @Test
    void execute_shouldReturnErrorMessageOnException() {
        ToolRegistry registry = new ToolRegistry();
        registry.register(
                new Tool("failing", "Failing tool", Map.of(), false),
                args -> { throw new RuntimeException("Test error"); }
        );

        Environment env = new Environment(registry);
        AgentAction action = new AgentAction(registry.findTool("failing"), Map.of());

        String result = env.execute(action);
        assertTrue(result.contains("ERROR"));
        assertTrue(result.contains("Test error"));
    }

    @Test
    void execute_withNullResult_shouldReturnNoResultMessage() {
        ToolRegistry registry = new ToolRegistry();
        registry.register(
                new Tool("voidTool", "Void tool", Map.of(), false),
                args -> null
        );

        Environment env = new Environment(registry);
        AgentAction action = new AgentAction(registry.findTool("voidTool"), Map.of());

        String result = env.execute(action);
        assertEquals("(no result)", result);
    }
}
