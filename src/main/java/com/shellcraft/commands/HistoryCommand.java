package com.shellcraft.commands;

import com.shellcraft.core.ParsedCommand;
import com.shellcraft.shell.ShellState;

import java.util.List;

public class HistoryCommand implements ShellCommand {

    @Override
    public int execute(ParsedCommand command, ShellState state) {
        List<String> history = state.getHistory();
        for (int i = 0; i < history.size(); i++) {
            System.out.printf("%4d  %s%n", i + 1, history.get(i));
        }
        return 0;
    }

    @Override
    public String getName() { return "history"; }
}
