package dev.kaeron.game.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kaeron.game.core.AgentAction;
import dev.kaeron.game.core.Tool;
import dev.kaeron.game.core.ToolRegistry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionParser {

    private static final Pattern ACTION_BLOCK_PATTERN =
            Pattern.compile("```action\\s*(\\{.*?})\\s*```", Pattern.DOTALL);

    private final ToolRegistry registry;
    private final ObjectMapper mapper;

    public ActionParser(ToolRegistry registry) {
        this.registry = registry;
        this.mapper = new ObjectMapper();
    }

    public AgentAction parse(String llmResponse) {
        try {
            Matcher matcher = ACTION_BLOCK_PATTERN.matcher(llmResponse);

            if (!matcher.find()) {
                System.out.println("⚠️  No ```action block found in LLM response");
                return fallbackTerminate("No action block in response: " + truncate(llmResponse, 200));
            }

            String json = matcher.group(1).trim();
            JsonNode node = mapper.readTree(json);

            String toolName = node.get("tool").asText();
            Map<String, Object> args = parseArgs(node.get("args"));

            Tool tool = registry.findTool(toolName);
            return new AgentAction(tool, args);

        } catch (Exception e) {
            System.out.println("⚠️  Error parsing LLM response: " + e.getMessage());
            return fallbackTerminate("Parse error: " + e.getMessage());
        }
    }

    private Map<String, Object> parseArgs(JsonNode argsNode) {
        Map<String, Object> args = new HashMap<>();
        if (argsNode == null || argsNode.isNull() || argsNode.isEmpty()) {
            return args;
        }

        Iterator<Map.Entry<String, JsonNode>> fields = argsNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            JsonNode value = field.getValue();

            if (value.isTextual())      args.put(field.getKey(), value.asText());
            else if (value.isNumber())  args.put(field.getKey(), value.asDouble());
            else if (value.isBoolean()) args.put(field.getKey(), value.asBoolean());
            else                        args.put(field.getKey(), value.toString());
        }
        return args;
    }

    private AgentAction fallbackTerminate(String message) {
        Tool fallback = new Tool(
                "terminate",
                "End the session",
                Map.of("message", "string"),
                true
        );
        return new AgentAction(fallback, Map.of("message", message));
    }

    private String truncate(String s, int max) {
        return s == null ? "" : s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
