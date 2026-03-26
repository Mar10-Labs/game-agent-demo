package dev.kaeron.game.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryTest {

    @Test
    void add_shouldStoreEntry() {
        Memory memory = new Memory();
        memory.add("user", "Hello");

        assertEquals(1, memory.size());
    }

    @Test
    void getRelevantMemories_shouldReturnAllEntries() {
        Memory memory = new Memory();
        memory.add("user", "First");
        memory.add("assistant", "Second");

        var memories = memory.getRelevantMemories();
        assertEquals(2, memories.size());
        assertEquals("user", memories.get(0).role());
        assertEquals("First", memories.get(0).content());
    }

    @Test
    void clear_shouldRemoveAllEntries() {
        Memory memory = new Memory();
        memory.add("user", "Hello");
        memory.add("assistant", "World");

        memory.clear();
        assertEquals(0, memory.size());
    }

    @Test
    void entry_shouldHaveCorrectRoleAndContent() {
        Memory memory = new Memory();
        memory.add("system", "You are helpful");

        var entry = memory.getRelevantMemories().get(0);
        assertEquals("system", entry.role());
        assertEquals("You are helpful", entry.content());
    }
}
