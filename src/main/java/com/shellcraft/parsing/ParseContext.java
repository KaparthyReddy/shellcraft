package com.shellcraft.parsing;

import com.shellcraft.core.ParsedCommand;
import com.shellcraft.core.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Mutable state threaded through the parsing chain. Each stage reads what
 * the previous stage produced and writes its own output here, rather than
 * every stage having a different method signature - this is what lets the
 * Chain of Responsibility stages stay decoupled from each other's
 * concrete types.
 */
public class ParseContext {

    private final String rawInput;
    private final Map<String, String> environment;

    private List<Token> tokens = new ArrayList<>();
    private List<List<Token>> segments = new ArrayList<>();
    private boolean background = false;
    private List<ParsedCommand> parsedCommands = new ArrayList<>();

    public ParseContext(String rawInput, Map<String, String> environment) {
        this.rawInput = rawInput;
        this.environment = environment;
    }

    public String getRawInput() { return rawInput; }
    public Map<String, String> getEnvironment() { return environment; }

    public List<Token> getTokens() { return tokens; }
    public void setTokens(List<Token> tokens) { this.tokens = tokens; }

    public List<List<Token>> getSegments() { return segments; }
    public void setSegments(List<List<Token>> segments) { this.segments = segments; }

    public boolean isBackground() { return background; }
    public void setBackground(boolean background) { this.background = background; }

    public List<ParsedCommand> getParsedCommands() { return parsedCommands; }
    public void setParsedCommands(List<ParsedCommand> parsedCommands) { this.parsedCommands = parsedCommands; }

    public Pipeline toPipeline() {
        return new Pipeline(parsedCommands, background);
    }
}
