package dev.kaeron.game.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ToolRegistryTest {

    private ToolRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ToolRegistry();
    }

    @Test
    void register_shouldFindRegisteredTool() {
        Tool tool = new Tool("testTool", "A test tool", Map.of(), false);
        registry.register(tool, args -> "result");

        Tool found = registry.findTool("testTool");
        assertEquals("testTool", found.name());
        assertEquals("A test tool", found.description());
    }

    @Test
    void register_shouldExecuteBinding() {
        Tool tool = new Tool("echo", "Echoes input", Map.of("msg", "string"), false);
        registry.register(tool, args -> "echo: " + args.get("msg"));

        Object result = registry.execute("echo", Map.of("msg", "hello"));
        assertEquals("echo: hello", result);
    }

    @Test
    void findTool_shouldThrowForUnknownTool() {
        assertThrows(IllegalArgumentException.class, () -> registry.findTool("unknown"));
    }

    @Test
    void execute_shouldThrowForUnknownTool() {
        assertThrows(IllegalArgumentException.class, () -> registry.execute("unknown", Map.of()));
    }

    @Test
    void allTools_shouldReturnAllRegistered() {
        registry.register(new Tool("tool1", "Tool 1", Map.of(), false), args -> null);
        registry.register(new Tool("tool2", "Tool 2", Map.of(), false), args -> null);

        assertEquals(2, registry.allTools().size());
    }
}
