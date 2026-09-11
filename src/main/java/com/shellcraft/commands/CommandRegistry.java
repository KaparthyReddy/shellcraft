package com.shellcraft.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Lookup table from command name -> ShellCommand implementation. This is
 * the piece that makes the Command pattern actually pay off: extending
 * the shell with a new built-in means writing one class and adding one
 * line here - ShellExecutor and the parser never need to change.
 */
public class CommandRegistry {

    private final Map<String, ShellCommand> commands = new HashMap<>();

    public CommandRegistry() {
        register(new CdCommand());
        register(new PwdCommand());
        register(new ExitCommand());
        register(new HistoryCommand());
        register(new ExportCommand());
        register(new JobsCommand());
    }

    public void register(ShellCommand command) {
        commands.put(command.getName(), command);
    }

    public Optional<ShellCommand> lookup(String name) {
        return Optional.ofNullable(commands.get(name));
    }

    public boolean isBuiltin(String name) {
        return commands.containsKey(name);
    }
}
