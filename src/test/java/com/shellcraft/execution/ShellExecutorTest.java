package com.shellcraft.execution;

import com.shellcraft.commands.CommandRegistry;
import com.shellcraft.core.ParsedCommand;
import com.shellcraft.core.Pipeline;
import com.shellcraft.shell.ShellState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShellExecutorTest {

    private final ShellExecutor executor = new ShellExecutor(new CommandRegistry(), new ExternalProcessRunner());

    @Test
    void executesBuiltinDirectly() {
        ShellState state = new ShellState();
        ParsedCommand pwd = new ParsedCommand("pwd", List.of(), List.of());
        Pipeline pipeline = new Pipeline(List.of(pwd), false);

        int exitCode = executor.execute(pipeline, state);

        assertEquals(0, exitCode);
    }

    @Test
    void executesRealExternalCommand() {
        ShellState state = new ShellState();
        ParsedCommand echo = new ParsedCommand("echo", List.of("test"), List.of());
        Pipeline pipeline = new Pipeline(List.of(echo), false);

        int exitCode = executor.execute(pipeline, state);

        assertEquals(0, exitCode);
    }

    @Test
    void returns127ForNonexistentCommand() {
        ShellState state = new ShellState();
        ParsedCommand bogus = new ParsedCommand("definitely_not_a_real_command_xyz", List.of(), List.of());
        Pipeline pipeline = new Pipeline(List.of(bogus), false);

        int exitCode = executor.execute(pipeline, state);

        assertEquals(127, exitCode);
    }
}
