package dev.kaeron.game.core;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ToolTest {

    @Test
    void formatForPrompt_withParameters_shouldShowParameters() {
        Tool tool = new Tool(
                "testTool",
                "A test tool",
                Map.of("param1", "string", "param2", "number"),
                false
        );

        String formatted = tool.formatForPrompt();
        assertTrue(formatted.contains("### testTool"));
        assertTrue(formatted.contains("A test tool"));
        assertTrue(formatted.contains("param1"));
        assertTrue(formatted.contains("param2"));
        assertTrue(formatted.contains("Parameters:"));
    }

    @Test
    void formatForPrompt_withoutParameters_shouldShowNone() {
        Tool tool = new Tool("simple", "A simple tool", Map.of(), false);
        String formatted = tool.formatForPrompt();
        assertTrue(formatted.contains("Parameters: none"));
    }

    @Test
    void terminal_shouldBeAccessible() {
        Tool terminalTool = new Tool("exit", "Exit", Map.of(), true);
        Tool normalTool = new Tool("action", "Action", Map.of(), false);

        assertTrue(terminalTool.terminal());
        assertFalse(normalTool.terminal());
    }

    @Test
    void record_shouldHaveCorrectValues() {
        Tool tool = new Tool("name", "desc", Map.of("key", "value"), true);
        assertEquals("name", tool.name());
        assertEquals("desc", tool.description());
        assertEquals(1, tool.parameters().size());
        assertTrue(tool.terminal());
    }
}
