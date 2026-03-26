package dev.kaeron.game.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.kaeron.game.config.AppConfig;
import okhttp3.*;

import java.util.List;
import java.util.Map;

public class GroqClient {

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final MediaType JSON_MEDIA = MediaType.get("application/json; charset=utf-8");

    private final String apiKey;
    private final String model;
    private final double temperature;
    private final int maxTokens;
    private final OkHttpClient httpClient;
    private final ObjectMapper mapper;

    public GroqClient(AppConfig config) {
        this.apiKey      = config.getRequired("GROQ_API_KEY");
        this.model       = config.getOrDefault("GROQ_MODEL", "llama-3.3-70b-versatile");
        this.temperature = config.getDouble("GROQ_TEMPERATURE", 0.1);
        this.maxTokens   = config.getInt("GROQ_MAX_TOKENS", 1024);
        this.httpClient  = new OkHttpClient.Builder()
                .callTimeout(java.time.Duration.ofSeconds(60))
                .build();
        this.mapper = new ObjectMapper();

        System.out.printf("🤖 LLM: %s | temperature=%.1f | max_tokens=%d%n",
                model, temperature, maxTokens);
    }

    public String chat(List<Map<String, String>> messages) {
        String requestBody = buildRequestBody(messages);

        Request request = new Request.Builder()
                .url(GROQ_URL)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, JSON_MEDIA))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body().string();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Groq API error %d: %s".formatted(response.code(), body));
            }
            JsonNode json = mapper.readTree(body);
            return json.at("/choices/0/message/content").asText();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error calling Groq API: " + e.getMessage(), e);
        }
    }

    private String buildRequestBody(List<Map<String, String>> messages) {
        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("model", model);
            root.put("temperature", temperature);
            root.put("max_tokens", maxTokens);

            ArrayNode messagesArray = mapper.createArrayNode();
            for (Map<String, String> msg : messages) {
                ObjectNode msgNode = mapper.createObjectNode();
                msgNode.put("role", msg.get("role"));
                msgNode.put("content", msg.get("content"));
                messagesArray.add(msgNode);
            }
            root.set("messages", messagesArray);

            return mapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException("Error building request body: " + e.getMessage(), e);
        }
    }
}
