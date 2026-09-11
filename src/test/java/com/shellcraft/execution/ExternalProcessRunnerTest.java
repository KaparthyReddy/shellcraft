package com.shellcraft.execution;

import com.shellcraft.core.ParsedCommand;
import com.shellcraft.core.Redirection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests against real OS processes (echo, cat) rather than mocking
 * ProcessBuilder itself - forking real processes and wiring real file
 * descriptors is the entire point of this class, so testing it any other
 * way would be testing something other than what the class actually does.
 */
class ExternalProcessRunnerTest {

    private final ExternalProcessRunner runner = new ExternalProcessRunner();

    @Test
    void runsRealEchoCommand(@TempDir Path tempDir) throws Exception {
        ParsedCommand command = new ParsedCommand("echo", List.of("hello"), List.of());
        ProcessBuilder builder = runner.buildProcessBuilder(command, tempDir.toFile());
        builder.redirectErrorStream(true);

        Process process = builder.start();
        String output = new String(process.getInputStream().readAllBytes()).trim();
        process.waitFor();

        assertEquals("hello", output);
    }

    @Test
    void appliesOutputRedirectionToRealFile(@TempDir Path tempDir) throws Exception {
        File outputFile = tempDir.resolve("out.txt").toFile();
        ParsedCommand command = new ParsedCommand(
                "echo", List.of("written to file"),
                List.of(new Redirection(Redirection.Type.OUTPUT_TRUNCATE, outputFile.getAbsolutePath()))
        );

        ProcessBuilder builder = runner.buildProcessBuilder(command, tempDir.toFile());
        Process process = builder.start();
        process.waitFor();

        String content = Files.readString(outputFile.toPath()).trim();
        assertEquals("written to file", content);
    }

    @Test
    void appendRedirectionAddsToExistingFile(@TempDir Path tempDir) throws Exception {
        File outputFile = tempDir.resolve("append.txt").toFile();
        Files.writeString(outputFile.toPath(), "first line\n");

        ParsedCommand command = new ParsedCommand(
                "echo", List.of("second line"),
                List.of(new Redirection(Redirection.Type.OUTPUT_APPEND, outputFile.getAbsolutePath()))
        );

        ProcessBuilder builder = runner.buildProcessBuilder(command, tempDir.toFile());
        builder.start().waitFor();

        String content = Files.readString(outputFile.toPath());
        assertTrue(content.contains("first line"));
        assertTrue(content.contains("second line"));
    }
}
