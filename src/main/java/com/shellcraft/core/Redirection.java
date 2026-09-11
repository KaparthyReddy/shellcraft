package com.shellcraft.core;

/** Represents a single I/O redirection: >, >>, or <. */
public final class Redirection {

    public enum Type { OUTPUT_TRUNCATE, OUTPUT_APPEND, INPUT }

    private final Type type;
    private final String targetFile;

    public Redirection(Type type, String targetFile) {
        this.type = type;
        this.targetFile = targetFile;
    }

    public Type getType() { return type; }
    public String getTargetFile() { return targetFile; }

    @Override
    public String toString() {
        String symbol = switch (type) {
            case OUTPUT_TRUNCATE -> ">";
            case OUTPUT_APPEND -> ">>";
            case INPUT -> "<";
        };
        return symbol + " " + targetFile;
    }
}
