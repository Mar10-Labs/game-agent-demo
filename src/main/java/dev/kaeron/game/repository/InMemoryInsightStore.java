package dev.kaeron.game.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryInsightStore implements InsightStore {

    private final List<Map<String, Object>> insights = new ArrayList<>();

    @Override
    public void save(Map<String, Object> insight) {
        insights.add(Map.copyOf(insight));
    }

    @Override
    public List<Map<String, Object>> findAll() {
        return List.copyOf(insights);
    }
}
