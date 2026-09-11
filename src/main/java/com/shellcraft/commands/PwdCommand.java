package com.shellcraft.commands;

import com.shellcraft.core.ParsedCommand;
import com.shellcraft.shell.ShellState;

public class PwdCommand implements ShellCommand {

    @Override
    public int execute(ParsedCommand command, ShellState state) {
        System.out.println(state.getCurrentDirectory().getAbsolutePath());
        return 0;
    }

    @Override
    public String getName() { return "pwd"; }
}
