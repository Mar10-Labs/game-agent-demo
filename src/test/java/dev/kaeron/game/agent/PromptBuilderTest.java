package dev.kaeron.game.agent;

import dev.kaeron.game.core.Goal;
import dev.kaeron.game.core.Memory;
import dev.kaeron.game.core.Tool;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PromptBuilderTest {

    private PromptBuilder builder = new PromptBuilder();

    @Test
    void build_shouldIncludeSystemMessage() {
        List<Goal> goals = List.of(
                new Goal(1, "Test Goal", "Test description")
        );
        List<Tool> tools = List.of(
                new Tool("testTool", "A test tool", Map.of(), false)
        );
        Memory memory = new Memory();

        var messages = builder.build(goals, tools, memory);

        assertFalse(messages.isEmpty());
        assertEquals("system", messages.get(0).get("role"));
        assertTrue(messages.get(0).get("content").contains("Test Goal"));
        assertTrue(messages.get(0).get("content").contains("testTool"));
    }

    @Test
    void build_shouldIncludeMemoryMessages() {
        List<Goal> goals = List.of();
        List<Tool> tools = List.of();
        Memory memory = new Memory();
        memory.add("user", "Hello");

        var messages = builder.build(goals, tools, memory);

        boolean hasUserMessage = messages.stream()
                .anyMatch(m -> "user".equals(m.get("role")) && m.get("content").contains("Hello"));
        assertTrue(hasUserMessage);
    }

    @Test
    void build_shouldOrderGoalsByPriority() {
        List<Goal> goals = List.of(
                new Goal(3, "Third", "Third desc"),
                new Goal(1, "First", "First desc"),
                new Goal(2, "Second", "Second desc")
        );

        var messages = builder.build(goals, List.of(), new Memory());
        String content = messages.get(0).get("content");

        int firstPos = content.indexOf("First");
        int secondPos = content.indexOf("Second");
        int thirdPos = content.indexOf("Third");

        assertTrue(firstPos < secondPos);
        assertTrue(secondPos < thirdPos);
    }
}
