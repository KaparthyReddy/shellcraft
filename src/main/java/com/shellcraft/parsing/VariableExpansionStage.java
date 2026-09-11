package com.shellcraft.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Expands $VAR references against the shell's environment map. Skips
 * tokens that came from single-quoted strings, matching real shell
 * semantics where single quotes suppress all expansion.
 */
public class VariableExpansionStage extends ParseStage {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$([A-Za-z_][A-Za-z0-9_]*)");

    @Override
    protected void handle(ParseContext context) {
        Map<String, String> environment = context.getEnvironment();
        List<Token> expanded = new ArrayList<>();

        for (Token token : context.getTokens()) {
            if (token.isSingleQuoted()) {
                expanded.add(token);
                continue;
            }
            expanded.add(new Token(expandVariables(token.getValue(), environment), false));
        }

        context.setTokens(expanded);
    }

    private String expandVariables(String value, Map<String, String> environment) {
        Matcher matcher = VARIABLE_PATTERN.matcher(value);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String variableName = matcher.group(1);
            String replacement = environment.getOrDefault(variableName, "");
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
