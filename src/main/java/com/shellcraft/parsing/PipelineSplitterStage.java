package com.shellcraft.parsing;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits the token stream into pipeline segments on "|", and detects a
 * trailing "&" marking the whole pipeline as a background job. This runs
 * before redirection extraction, since redirections are per-segment and
 * need segments to already exist.
 */
public class PipelineSplitterStage extends ParseStage {

    @Override
    protected void handle(ParseContext context) {
        List<Token> tokens = new ArrayList<>(context.getTokens());

        if (!tokens.isEmpty() && tokens.get(tokens.size() - 1).getValue().equals("&")) {
            context.setBackground(true);
            tokens.remove(tokens.size() - 1);
        }

        List<List<Token>> segments = new ArrayList<>();
        List<Token> currentSegment = new ArrayList<>();

        for (Token token : tokens) {
            if (token.getValue().equals("|")) {
                segments.add(currentSegment);
                currentSegment = new ArrayList<>();
            } else {
                currentSegment.add(token);
            }
        }
        segments.add(currentSegment);

        context.setSegments(segments);
    }
}
