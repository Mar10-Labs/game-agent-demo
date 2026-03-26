package dev.kaeron.game.repository;

import java.util.List;
import java.util.Map;

public interface InsightStore {
    void save(Map<String, Object> insight);
    List<Map<String, Object>> findAll();
}
