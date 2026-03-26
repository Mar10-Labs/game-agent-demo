package dev.kaeron.game.core;

import java.util.ArrayList;
import java.util.List;

public class Memory {

    public record Entry(String role, String content) {}

    private final List<Entry> entries = new ArrayList<>();

    public void add(String role, String content) {
        entries.add(new Entry(role, content));
    }

    public List<Entry> getRelevantMemories() {
        return List.copyOf(entries);
    }

    public int size() {
        return entries.size();
    }

    public void clear() {
        entries.clear();
    }
}
