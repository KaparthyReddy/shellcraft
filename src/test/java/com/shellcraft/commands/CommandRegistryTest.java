package com.shellcraft.commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandRegistryTest {

    private final CommandRegistry registry = new CommandRegistry();

    @Test
    void recognizesAllExpectedBuiltins() {
        assertTrue(registry.isBuiltin("cd"));
        assertTrue(registry.isBuiltin("pwd"));
        assertTrue(registry.isBuiltin("exit"));
        assertTrue(registry.isBuiltin("history"));
        assertTrue(registry.isBuiltin("export"));
        assertTrue(registry.isBuiltin("jobs"));
    }

    @Test
    void doesNotRecognizeExternalCommandsAsBuiltins() {
        assertFalse(registry.isBuiltin("ls"));
        assertFalse(registry.isBuiltin("grep"));
    }

    @Test
    void lookupReturnsCorrectImplementation() {
        var command = registry.lookup("pwd").orElseThrow();
        assertEquals("pwd", command.getName());
        assertInstanceOf(PwdCommand.class, command);
    }

    @Test
    void lookupReturnsEmptyForUnknownCommand() {
        assertTrue(registry.lookup("nonexistent_command").isEmpty());
    }
}
