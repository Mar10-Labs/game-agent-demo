package dev.kaeron.game.repository;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryInsightStoreTest {

    @Test
    void save_shouldStoreInsight() {
        InsightStore store = new InMemoryInsightStore();
        store.save(Map.of("category", "AI", "insight", "Test insight", "relevanceScore", 8));

        var insights = store.findAll();
        assertEquals(1, insights.size());
        assertEquals("AI", insights.get(0).get("category"));
    }

    @Test
    void save_multipleInsights_shouldStoreAll() {
        InsightStore store = new InMemoryInsightStore();
        store.save(Map.of("category", "AI", "insight", "First"));
        store.save(Map.of("category", "DB", "insight", "Second"));

        var insights = store.findAll();
        assertEquals(2, insights.size());
    }

    @Test
    void findAll_shouldReturnImmutableCopy() {
        InsightStore store = new InMemoryInsightStore();
        store.save(Map.of("test", "value"));

        var insights = store.findAll();
        assertThrows(UnsupportedOperationException.class, () -> {
            insights.add(Map.of("another", "value"));
        });
    }
}
