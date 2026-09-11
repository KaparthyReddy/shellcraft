package com.shellcraft.commands;

import com.shellcraft.core.ParsedCommand;
import com.shellcraft.shell.ShellState;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CdCommandTest {

    @Test
    void changesToValidDirectory() {
        ShellState state = new ShellState();
        state.setCurrentDirectory(new File(System.getProperty("java.io.tmpdir")));

        ParsedCommand command = new ParsedCommand("cd", List.of(System.getProperty("user.home")), List.of());
        int exitCode = new CdCommand().execute(command, state);

        assertEquals(0, exitCode);
        assertEquals(new File(System.getProperty("user.home")).getAbsolutePath(),
                state.getCurrentDirectory().getAbsolutePath());
    }

    @Test
    void failsGracefullyOnNonexistentDirectory() {
        ShellState state = new ShellState();
        File originalDir = state.getCurrentDirectory();

        ParsedCommand command = new ParsedCommand("cd", List.of("/definitely/does/not/exist/xyz"), List.of());
        int exitCode = new CdCommand().execute(command, state);

        assertEquals(1, exitCode);
        assertEquals(originalDir, state.getCurrentDirectory()); // unchanged on failure
    }

    @Test
    void noArgumentGoesHome() {
        ShellState state = new ShellState();
        ParsedCommand command = new ParsedCommand("cd", List.of(), List.of());

        new CdCommand().execute(command, state);

        assertEquals(new File(System.getProperty("user.home")).getAbsolutePath(),
                state.getCurrentDirectory().getAbsolutePath());
    }
}
