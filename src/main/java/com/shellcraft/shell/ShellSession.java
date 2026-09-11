package com.shellcraft.shell;

import com.shellcraft.commands.CommandRegistry;
import com.shellcraft.core.Pipeline;
import com.shellcraft.execution.ExternalProcessRunner;
import com.shellcraft.execution.ShellExecutor;
import com.shellcraft.parsing.ShellParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The read-eval-print loop: reads a line, parses it into a Pipeline,
 * executes it, repeats until exit is requested. This class deliberately
 * knows nothing about parsing internals or process management - it just
 * wires ShellParser and ShellExecutor together and handles the
 * top-level error boundary so one bad command doesn't crash the whole
 * session.
 */
public class ShellSession {

    private final ShellParser parser;
    private final ShellExecutor executor;
    private final ShellState state;

    public ShellSession() {
        this.parser = new ShellParser();
        CommandRegistry registry = new CommandRegistry();
        this.executor = new ShellExecutor(registry, new ExternalProcessRunner());
        this.state = new ShellState();
    }

    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (!state.isExitRequested()) {
            printPrompt();
            String line;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                break; // stdin closed (e.g. piped input ended, or Ctrl+D)
            }

            if (line == null) break; // EOF
            if (line.isBlank()) continue;

            state.addToHistory(line);
            runOneLine(line);
        }

        System.out.println("exit");
    }

    private void runOneLine(String line) {
        try {
            Pipeline pipeline = parser.parse(line, state.getEnvironment());
            executor.execute(pipeline, state);
        } catch (IllegalArgumentException e) {
            System.err.println("shellcraft: " + e.getMessage());
        } catch (UnsupportedOperationException e) {
            System.err.println("shellcraft: " + e.getMessage());
        }
    }

    private void printPrompt() {
        System.out.print(state.getCurrentDirectory().getName() + " $ ");
        System.out.flush();
    }
}
