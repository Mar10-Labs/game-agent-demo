package dev.kaeron.game.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoalTest {

    @Test
    void formatForPrompt_shouldFormatCorrectly() {
        Goal goal = new Goal(1, "Test Goal", "This is a test");
        String formatted = goal.formatForPrompt();

        assertTrue(formatted.contains("## Goal 1: Test Goal"));
        assertTrue(formatted.contains("This is a test"));
    }

    @Test
    void priority_shouldBeAccessible() {
        Goal goal = new Goal(5, "Low Priority", "Description");
        assertEquals(5, goal.priority());
    }

    @Test
    void name_shouldBeAccessible() {
        Goal goal = new Goal(1, "My Goal", "Description");
        assertEquals("My Goal", goal.name());
    }

    @Test
    void description_shouldBeAccessible() {
        Goal goal = new Goal(1, "Name", "My Description");
        assertEquals("My Description", goal.description());
    }
}
