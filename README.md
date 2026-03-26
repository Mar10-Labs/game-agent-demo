# GAME Agent Framework — Tech News Analyzer

Implementation of the GAME (Goals · Actions · Memory · Environment) methodology for building AI agents in Java 21.

An AI agent that reads the day's tech news and generates a report for backend engineers, demonstrating how to separate agent loop logic from business domain.

Production-ready code with solid architecture principles: interface/implementation separation, externalized configuration, extensibility via inheritance or composition. The core agent loop is under 15 lines—every component is visible and debuggable.

## Setup in IntelliJ

### Requirements
- Java 21+
- IntelliJ IDEA (Community or Ultimate)
- Groq API key (free at [console.groq.com](https://console.groq.com))

### Steps

**1. Open the project**
```
File → Open → select the game-agent-demo folder
```
IntelliJ detects the `pom.xml` automatically and downloads dependencies.

**2. Configure the API key**

Option A — `.env` file (recommended):
```bash
cp .env.example .env
# Edit .env and paste your GROQ_API_KEY
```

Option B — IntelliJ Run Configuration:
```
Run → Edit Configurations → NewsAnalyzerDemo
→ Environment Variables → GROQ_API_KEY=gsk_xxxx
```

**3. Run**
```
Right-click on NewsAnalyzerDemo.java → Run 'NewsAnalyzerDemo.main()'
```

## How the AI Agent Works

This project uses **Groq** (a fast LLM API) as the "brain" of the agent. Here's how it works:

**1. The agent loop:**
```
User goal → Groq decides next tool → Execute tool → Repeat until done
```

**2. What Groq receives each iteration:**
- The user's goal (e.g., "Analyze today's tech news")
- Available tools (`fetchHeadlines`, `readArticle`, `categorizeInsight`, `terminate`)
- Session memory (what the agent has already done)
- Current environment state

**3. What Groq returns:**
A JSON action like:
```json
{
  "tool": "fetchHeadlines",
  "args": {}
}
```

**4. The agent executes the tool and loops back to Groq** with the updated context.

Groq does **not** generate content or code—it decides **which tool to call** based on the context. This is a tool-calling pattern (similar to OpenAI's function calling).

The main loop is in `GameAgent.java` (~15 lines), making it easy to understand and debug.

## Project Structure

```
src/main/java/dev/kaeron/game/
│
├── core/                    ← The 4 GAME components (pure framework)
│   ├── Goal.java            ← G: what to achieve and how to behave
│   ├── Tool.java            ← A: definition of a capability
│   ├── AgentAction.java     ← concrete decision from the LLM
│   ├── Memory.java          ← M: session history
│   ├── ToolRegistry.java    ← maps names to implementations
│   └── Environment.java     ← E: executes the actions
│
├── agent/                   ← Agent infrastructure
│   ├── GroqClient.java      ← HTTP client for Groq API
│   ├── PromptBuilder.java   ← GAME → messages for the LLM
│   ├── ActionParser.java    ← LLM response → AgentAction
│   └── GameAgent.java       ← ⭐ the main loop
│
├── tools/                   ← Domain tools (interchangeable)
│   └── NewsTools.java       ← fetchHeadlines, readArticle, etc.
│
└── demo/
    └── NewsAnalyzerDemo.java ← main() — wiring everything together
```

## How to Extend

**Add a new tool:**
```java
Tool myTool = new Tool("myTool", "description", Map.of("param", "type"), false);
registry.register(myTool, args -> {
    // implementation
    return result;
});
```

**Change the model:**
```java
new GroqClient(apiKey, "llama-3.1-8b-instant")  // faster
new GroqClient(apiKey, "mixtral-8x7b-32768")     // more context
```

**Sliding window memory:**
```java
public class SlidingMemory extends Memory {
    @Override
    public List<Entry> getRelevantMemories() {
        var all = super.getRelevantMemories();
        int window = 10;
        return all.size() <= window ? all : all.subList(all.size() - window, all.size());
    }
}
```

## Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| jackson-databind | 2.17.1 | JSON serialization |
| okhttp | 4.12.0 | HTTP client for Groq |
| dotenv-java | 3.0.0 | Read `.env` file |
