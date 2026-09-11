package com.shellcraft.commands;

import com.shellcraft.core.ParsedCommand;
import com.shellcraft.shell.ShellState;

public class ExportCommand implements ShellCommand {

    @Override
    public int execute(ParsedCommand command, ShellState state) {
        if (command.getArguments().isEmpty()) {
            System.err.println("export: usage: export VAR=value");
            return 1;
        }

        for (String arg : command.getArguments()) {
            int equalsIndex = arg.indexOf('=');
            if (equalsIndex < 0) {
                System.err.println("export: invalid syntax: " + arg);
                return 1;
            }
            String key = arg.substring(0, equalsIndex);
            String value = arg.substring(equalsIndex + 1);
            state.getEnvironment().put(key, value);
        }
        return 0;
    }

    @Override
    public String getName() { return "export"; }
}
