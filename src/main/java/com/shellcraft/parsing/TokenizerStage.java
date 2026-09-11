package com.shellcraft.parsing;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits the raw input line into tokens, respecting single and double
 * quotes (whitespace inside quotes doesn't split the token) and treating
 * shell metacharacters (| > < &) as their own standalone tokens even
 * when not surrounded by whitespace (e.g. "cmd>file" tokenizes the same
 * as "cmd > file").
 */
public class TokenizerStage extends ParseStage {

    private static final String METACHARACTERS = "|<>&";

    @Override
    protected void handle(ParseContext context) {
        List<Token> tokens = new ArrayList<>();
        String input = context.getRawInput();

        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                    tokens.add(new Token(current.toString(), true));
                    current.setLength(0);
                } else {
                    current.append(c);
                }
                continue;
            }

            if (inDoubleQuote) {
                if (c == '"') {
                    inDoubleQuote = false;
                    tokens.add(new Token(current.toString(), false));
                    current.setLength(0);
                } else {
                    current.append(c);
                }
                continue;
            }

            if (c == '\'') {
                flushIfNonEmpty(tokens, current);
                inSingleQuote = true;
                continue;
            }
            if (c == '"') {
                flushIfNonEmpty(tokens, current);
                inDoubleQuote = true;
                continue;
            }

            if (Character.isWhitespace(c)) {
                flushIfNonEmpty(tokens, current);
                continue;
            }

            if (METACHARACTERS.indexOf(c) >= 0) {
                flushIfNonEmpty(tokens, current);
                // handle >> as one token, not two separate '>' tokens
                if (c == '>' && i + 1 < input.length() && input.charAt(i + 1) == '>') {
                    tokens.add(new Token(">>", false));
                    i++;
                } else {
                    tokens.add(new Token(String.valueOf(c), false));
                }
                continue;
            }

            current.append(c);
        }
        flushIfNonEmpty(tokens, current);

        context.setTokens(tokens);
    }

    private void flushIfNonEmpty(List<Token> tokens, StringBuilder current) {
        if (current.length() > 0) {
            tokens.add(new Token(current.toString(), false));
            current.setLength(0);
        }
    }
}
