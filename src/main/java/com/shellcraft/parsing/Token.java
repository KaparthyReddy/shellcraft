package com.shellcraft.parsing;

/**
 * A single lexical token from tokenizing. Tracks whether it came from a
 * single-quoted string, since single-quoted content must NOT undergo
 * variable expansion later (matches real shell semantics: 'echo $HOME'
 * prints literally "$HOME", while "echo $HOME" or echo $HOME expands it).
 */
public final class Token {
    private final String value;
    private final boolean singleQuoted;

    public Token(String value, boolean singleQuoted) {
        this.value = value;
        this.singleQuoted = singleQuoted;
    }

    public String getValue() { return value; }
    public boolean isSingleQuoted() { return singleQuoted; }

    @Override
    public String toString() { return value; }
}
