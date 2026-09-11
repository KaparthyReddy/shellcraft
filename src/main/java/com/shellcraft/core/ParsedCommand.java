package com.shellcraft.core;

import java.util.List;
import java.util.Optional;

/** One command in a pipeline: a program name, its arguments, and any redirections attached to it. */
public final class ParsedCommand {

    private final String programName;
    private final List<String> arguments;
    private final List<Redirection> redirections;

    public ParsedCommand(String programName, List<String> arguments, List<Redirection> redirections) {
        this.programName = programName;
        this.arguments = List.copyOf(arguments);
        this.redirections = List.copyOf(redirections);
    }

    public String getProgramName() { return programName; }
    public List<String> getArguments() { return arguments; }
    public List<Redirection> getRedirections() { return redirections; }

    public Optional<Redirection> getInputRedirection() {
        return redirections.stream().filter(r -> r.getType() == Redirection.Type.INPUT).findFirst();
    }

    public Optional<Redirection> getOutputRedirection() {
        return redirections.stream()
                .filter(r -> r.getType() != Redirection.Type.INPUT)
                .findFirst();
    }

    /** Full argv, including the program name itself as argv[0] - the shape ProcessBuilder expects. */
    public List<String> toFullCommandLine() {
        List<String> full = new java.util.ArrayList<>();
        full.add(programName);
        full.addAll(arguments);
        return full;
    }

    @Override
    public String toString() {
        return programName + " " + String.join(" ", arguments)
                + (redirections.isEmpty() ? "" : " " + redirections);
    }
}
