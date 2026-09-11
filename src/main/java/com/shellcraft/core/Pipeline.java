package com.shellcraft.core;

import java.util.List;

/** A full parsed command line: one or more commands connected by pipes, and whether it runs in the background. */
public final class Pipeline {

    private final List<ParsedCommand> stages;
    private final boolean background;

    public Pipeline(List<ParsedCommand> stages, boolean background) {
        if (stages.isEmpty()) {
            throw new IllegalArgumentException("A pipeline must have at least one stage");
        }
        this.stages = List.copyOf(stages);
        this.background = background;
    }

    public List<ParsedCommand> getStages() { return stages; }
    public boolean isBackground() { return background; }
    public boolean isSingleCommand() { return stages.size() == 1; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stages.size(); i++) {
            sb.append(stages.get(i));
            if (i < stages.size() - 1) sb.append(" | ");
        }
        if (background) sb.append(" &");
        return sb.toString();
    }
}
