package dev.kaeron.game.config;

import io.github.cdimascio.dotenv.Dotenv;

public class AppConfig {

    private final Dotenv dotenv;

    public AppConfig() {
        this.dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
    }

    public String get(String key) {
        String value = dotenv.get(key);
        if (value != null && !value.isBlank()) return value;

        String envValue = System.getenv(key);
        return (envValue != null && !envValue.isBlank()) ? envValue : null;
    }

    public String getRequired(String key) {
        String value = get(key);
        if (value == null) {
            throw new IllegalStateException(missingKeyMessage(key));
        }
        return value;
    }

    public int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            System.out.printf("⚠️  '%s' is not a valid number ('%s'), using default %d%n", key, value, defaultValue);
            return defaultValue;
        }
    }

    public double getDouble(String key, double defaultValue) {
        String value = get(key);
        if (value == null) return defaultValue;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            System.out.printf("⚠️  '%s' is not a valid number ('%s'), using default %.1f%n", key, value, defaultValue);
            return defaultValue;
        }
    }

    public String getOrDefault(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    private String missingKeyMessage(String key) {
        return """
                
                ┌─────────────────────────────────────────────────────┐
                │  ❌ Required variable not found: %s
                ├─────────────────────────────────────────────────────┤
                │  Add it to the .env file in the project root:       │
                │    %s=value                                           │
                │                                                       │
                │  Or in IntelliJ → Run → Edit Configurations          │
                │    → Environment Variables                           │
                └─────────────────────────────────────────────────────┘
                """.formatted(key, key);
    }
}
