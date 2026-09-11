package com.shellcraft.parsing;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VariableExpansionStageTest {

    @Test
    void expandsKnownVariable() {
        ParseContext context = new ParseContext("ignored", Map.of("HOME", "/Users/test"));
        context.setTokens(List.of(new Token("$HOME/docs", false)));

        new VariableExpansionStage().handle(context);

        assertEquals("/Users/test/docs", context.getTokens().get(0).getValue());
    }

    @Test
    void unknownVariableExpandsToEmptyString() {
        ParseContext context = new ParseContext("ignored", Map.of());
        context.setTokens(List.of(new Token("$UNKNOWN_VAR", false)));

        new VariableExpansionStage().handle(context);

        assertEquals("", context.getTokens().get(0).getValue());
    }

    @Test
    void singleQuotedTokenIsNotExpanded() {
        ParseContext context = new ParseContext("ignored", Map.of("HOME", "/Users/test"));
        context.setTokens(List.of(new Token("$HOME", true)));

        new VariableExpansionStage().handle(context);

        assertEquals("$HOME", context.getTokens().get(0).getValue());
    }
}
