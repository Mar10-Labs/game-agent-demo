[![Medium](https://img.shields.io/badge/Medium-Read%20Article-black?style=for-the-badge&logo=medium&logoColor=white)](https://medium.com/@magam.2004/beyond-the-prompt-the-game-ae119910da90)
[![Java](https://img.shields.io/badge/Java-21+-blue?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![LLM](https://img.shields.io/badge/LLM-Groq%20/%20Llama%203.1-orange?style=for-the-badge&logo=meta&logoColor=white)](https://console.groq.com)
[![Methodology](https://img.shields.io/badge/Methodology-GAME-red?style=for-the-badge)](https://medium.com/@magam.2004/beyond-the-prompt-the-game-ae119910da90)


# GAME Agent — Tech News Analyzer

An AI agent that reads tech news and generates a structured report, built to demonstrate the GAME methodology: Goals · Actions · Memory · Environment.

The goal is simple: show how separating concerns (what to do, how to do it, what happened, where it runs) makes agents easier to build, test, and debug.


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

**GAME organizes your code. The LLM reasons about it.**

GAME has 4 components that live in your code:
- **G**oals → what the agent needs to achieve
- **A**ctions → what tools are available
- **M**emory → what happened so far
- **E**nvironment → where actions actually run

The LLM (Groq) is just the **reasoner** — it decides which Action to take based on the context you give it.

**The agent loop:**
```
Goal + Actions + Memory → LLM decides → Environment executes → Memory updates → Repeat
```

**What the LLM receives each iteration:**
- The **Goal** (what to achieve)
- The available **Actions** (tools)
- **Memory** (what the agent has already done)
- The result of the last action

**What the LLM returns:**
```json
{
  "tool": "fetchHeadlines",
  "args": {}
}
```

The LLM does **not** generate content — it only decides **which tool to call**. This is a tool-calling pattern (similar to OpenAI's function calling).

The main loop is in `GameAgent.java` (~15 lines), making it easy to understand and debug.

## Project Structure

```
src/main/java/dev/kaeron/game/
│
├── core/                    ← The 4 GAME components
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
