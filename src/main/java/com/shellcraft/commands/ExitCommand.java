package com.shellcraft.commands;

import com.shellcraft.core.ParsedCommand;
import com.shellcraft.shell.ShellState;

public class ExitCommand implements ShellCommand {

    @Override
    public int execute(ParsedCommand command, ShellState state) {
        state.requestExit();
        return 0;
    }

    @Override
    public String getName() { return "exit"; }
}
