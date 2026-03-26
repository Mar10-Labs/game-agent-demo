package dev.kaeron.game.agent;

import dev.kaeron.game.core.AgentAction;
import dev.kaeron.game.core.Tool;
import dev.kaeron.game.core.ToolRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ActionParserTest {

    private ActionParser parser;
    private ToolRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ToolRegistry();
        registry.register(
                new Tool("testTool", "A test tool", Map.of("param", "string"), false),
                args -> "result"
        );
        parser = new ActionParser(registry);
    }

    @Test
    void parse_validAction_shouldReturnAgentAction() {
        String llmResponse = """
                ```action
                {
                  "tool": "testTool",
                  "args": { "param": "value" }
                }
                ```
                """;

        AgentAction action = parser.parse(llmResponse);
        assertEquals("testTool", action.toolName());
        assertFalse(action.isTerminal());
        assertEquals("value", action.args().get("param"));
    }

    @Test
    void parse_terminalAction_shouldReturnTerminalAction() {
        registry.register(
                new Tool("terminate", "End session", Map.of(), true),
                args -> "done"
        );
        parser = new ActionParser(registry);

        String llmResponse = """
                ```action
                {
                  "tool": "terminate",
                  "args": {}
                }
                ```
                """;

        AgentAction action = parser.parse(llmResponse);
        assertTrue(action.isTerminal());
    }

    @Test
    void parse_noActionBlock_shouldReturnFallback() {
        String llmResponse = "No action block here";

        AgentAction action = parser.parse(llmResponse);
        assertTrue(action.isTerminal());
        assertTrue(action.toolName().equals("terminate"));
    }

    @Test
    void parse_invalidJson_shouldReturnFallback() {
        String llmResponse = """
                ```action
                { invalid json }
                ```
                """;

        AgentAction action = parser.parse(llmResponse);
        assertTrue(action.isTerminal());
    }

    @Test
    void parse_numericArgs_shouldParseAsDouble() {
        String llmResponse = """
                ```action
                {
                  "tool": "testTool",
                  "args": { "score": 9.5 }
                }
                ```
                """;

        AgentAction action = parser.parse(llmResponse);
        assertEquals(9.5, action.args().get("score"));
    }

    @Test
    void parse_booleanArgs_shouldParseCorrectly() {
        registry.register(
                new Tool("flagTool", "Flag tool", Map.of("enabled", "boolean"), false),
                args -> "result"
        );
        parser = new ActionParser(registry);

        String llmResponse = """
                ```action
                {
                  "tool": "flagTool",
                  "args": { "enabled": true }
                }
                ```
                """;

        AgentAction action = parser.parse(llmResponse);
        assertEquals(true, action.args().get("enabled"));
    }
}
