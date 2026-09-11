package com.shellcraft.commands;

import com.shellcraft.core.ParsedCommand;
import com.shellcraft.shell.ShellState;

import java.io.File;

public class CdCommand implements ShellCommand {

    @Override
    public int execute(ParsedCommand command, ShellState state) {
        String target = command.getArguments().isEmpty()
                ? state.getEnvironment().getOrDefault("HOME", System.getProperty("user.home"))
                : command.getArguments().get(0);

        File newDir = target.startsWith("/")
                ? new File(target)
                : new File(state.getCurrentDirectory(), target);

        if (!newDir.isDirectory()) {
            System.err.println("cd: no such directory: " + target);
            return 1;
        }

        state.setCurrentDirectory(newDir.getAbsoluteFile());
        return 0;
    }

    @Override
    public String getName() { return "cd"; }
}
