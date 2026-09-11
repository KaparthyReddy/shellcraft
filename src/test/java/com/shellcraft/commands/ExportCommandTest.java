package com.shellcraft.commands;

import com.shellcraft.core.ParsedCommand;
import com.shellcraft.shell.ShellState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExportCommandTest {

    @Test
    void setsEnvironmentVariable() {
        ShellState state = new ShellState();
        ParsedCommand command = new ParsedCommand("export", List.of("MY_VAR=hello"), List.of());

        int exitCode = new ExportCommand().execute(command, state);

        assertEquals(0, exitCode);
        assertEquals("hello", state.getEnvironment().get("MY_VAR"));
    }

    @Test
    void failsOnMissingEquals() {
        ShellState state = new ShellState();
        ParsedCommand command = new ParsedCommand("export", List.of("INVALID"), List.of());

        int exitCode = new ExportCommand().execute(command, state);

        assertEquals(1, exitCode);
    }
}
