package com.shellcraft.parsing;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenizerStageTest {

    private List<Token> tokenize(String input) {
        ParseContext context = new ParseContext(input, Map.of());
        new TokenizerStage().handle(context);
        return context.getTokens();
    }

    @Test
    void splitsOnWhitespace() {
        List<Token> tokens = tokenize("echo hello world");
        assertEquals(List.of("echo", "hello", "world"), tokens.stream().map(Token::getValue).toList());
    }

    @Test
    void keepsQuotedStringAsOneToken() {
        List<Token> tokens = tokenize("echo \"hello world\"");
        assertEquals(List.of("echo", "hello world"), tokens.stream().map(Token::getValue).toList());
    }

    @Test
    void marksSingleQuotedTokenCorrectly() {
        List<Token> tokens = tokenize("echo '$HOME'");
        assertEquals("$HOME", tokens.get(1).getValue());
        assertTrue(tokens.get(1).isSingleQuoted());
    }

    @Test
    void doubleQuotedTokenIsNotMarkedSingleQuoted() {
        List<Token> tokens = tokenize("echo \"$HOME\"");
        assertFalse(tokens.get(1).isSingleQuoted());
    }

    @Test
    void metacharactersAreSeparateTokensEvenWithoutSpaces() {
        List<Token> tokens = tokenize("cmd>file");
        assertEquals(List.of("cmd", ">", "file"), tokens.stream().map(Token::getValue).toList());
    }

    @Test
    void recognizesAppendOperatorAsSingleToken() {
        List<Token> tokens = tokenize("cmd >> file");
        assertEquals(List.of("cmd", ">>", "file"), tokens.stream().map(Token::getValue).toList());
    }

    @Test
    void recognizesPipeAndBackground() {
        List<Token> tokens = tokenize("cmd1 | cmd2 &");
        assertEquals(List.of("cmd1", "|", "cmd2", "&"), tokens.stream().map(Token::getValue).toList());
    }
}
