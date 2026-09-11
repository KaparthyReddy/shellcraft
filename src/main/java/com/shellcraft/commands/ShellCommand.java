package com.shellcraft.commands;

import com.shellcraft.core.ParsedCommand;
import com.shellcraft.shell.ShellState;

/**
 * Command pattern interface: every built-in implements this uniformly,
 * so ShellExecutor never needs a growing if/else chain to dispatch
 * built-ins - adding a new one is registering it in CommandRegistry,
 * nothing else changes.
 */
public interface ShellCommand {

    /** @return the exit code (0 for success, non-zero for failure) */
    int execute(ParsedCommand command, ShellState state);

    String getName();
}
