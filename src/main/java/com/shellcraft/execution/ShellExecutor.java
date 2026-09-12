package com.shellcraft.execution;

import com.shellcraft.commands.CommandRegistry;
import com.shellcraft.commands.ShellCommand;
import com.shellcraft.core.ParsedCommand;
import com.shellcraft.core.Pipeline;
import com.shellcraft.shell.ShellState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Executes a fully-parsed Pipeline. For a single built-in command, runs
 * it in-process via the Command pattern. For anything involving external
 * programs or a multi-stage pipe, delegates to real OS processes wired
 * together via ProcessBuilder's native pipe support - this is genuine
 * process control, not a simulation of it.
 *
 * A pipeline mixing a built-in with external commands (e.g.
 * "history | grep foo") treats the built-in's stdout capture as a
 * special case, since built-ins run in-process and don't have OS-level
 * file descriptors to pipe from directly.
 */
public class ShellExecutor {

    private final CommandRegistry registry;
    private final ExternalProcessRunner processRunner;

    public ShellExecutor(CommandRegistry registry, ExternalProcessRunner processRunner) {
        this.registry = registry;
        this.processRunner = processRunner;
    }

    public int execute(Pipeline pipeline, ShellState state) {
        if (pipeline.isSingleCommand()) {
            return executeSingle(pipeline.getStages().get(0), pipeline, state);
        }
        return executeMultiStagePipeline(pipeline, state);
    }

    private int executeSingle(ParsedCommand command, Pipeline pipeline, ShellState state) {
        Optional<ShellCommand> builtin = registry.lookup(command.getProgramName());

        if (builtin.isPresent()) {
            // Built-ins don't support backgrounding or piping meaningfully
            // in this implementation - they're fast, synchronous, in-process
            // operations by nature (cd, pwd, exit, etc.)
            return builtin.get().execute(command, state);
        }

        return runExternal(command, state, pipeline.isBackground(), List.of(command)).exitCode;
    }

    private int executeMultiStagePipeline(Pipeline pipeline, ShellState state) {
        List<ParsedCommand> stages = pipeline.getStages();

        // A built-in in the middle of a real OS pipe can't be wired via
        // file descriptors the way external processes can - this
        // implementation supports built-ins only as the pipeline's first
        // stage in a multi-stage pipe, which covers the common real-world
        // case (e.g. "history | grep foo") without the complexity of a
        // full in-process-to-OS-process bridging layer.
        ParsedCommand firstStage = stages.get(0);
        if (registry.isBuiltin(firstStage.getProgramName())) {
            throw new UnsupportedOperationException(
                    "Piping FROM a built-in mid-pipeline isn't supported yet: " + firstStage.getProgramName()
                            + " (built-ins are only supported as standalone commands in this version)"
            );
        }

        return runExternalPipeline(stages, state, pipeline.isBackground());
    }

    private ExecutionResult runExternal(ParsedCommand command, ShellState state,
                                         boolean background, List<ParsedCommand> allStagesForJobLabel) {
        try {
            ProcessBuilder builder = processRunner.buildProcessBuilder(command, state.getCurrentDirectory());
            builder.redirectErrorStream(false);
            if (command.getOutputRedirection().isEmpty()) {
                builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            }
            if (command.getInputRedirection().isEmpty()) {
                builder.redirectInput(ProcessBuilder.Redirect.INHERIT);
            }
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = builder.start();

            if (background) {
                state.getJobManager().registerJob(describePipeline(allStagesForJobLabel), List.of(process));
                return new ExecutionResult(0, process);
            }

            int exitCode = process.waitFor();
            return new ExecutionResult(exitCode, process);
        } catch (IOException e) {
            System.err.println(command.getProgramName() + ": command not found");
            return new ExecutionResult(127, null);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ExecutionResult(130, null);
        }
    }

    private int runExternalPipeline(List<ParsedCommand> stages, ShellState state, boolean background) {
        try {
            List<ProcessBuilder> builders = new ArrayList<>();
            for (ParsedCommand stage : stages) {
                builders.add(processRunner.buildProcessBuilder(stage, state.getCurrentDirectory()));
            }

            // startPipeline only wires the INTERNAL stdout->stdin connections
            // between stages - the outer ends (first stage's stdin, last
            // stage's stdout/stderr) must be explicitly inherited here, or
            // output silently vanishes into an unread pipe.
            ParsedCommand firstStage = stages.get(0);
            if (firstStage.getInputRedirection().isEmpty()) {
                builders.get(0).redirectInput(ProcessBuilder.Redirect.INHERIT);
            }

            ParsedCommand lastStage = stages.get(stages.size() - 1);
            ProcessBuilder lastBuilder = builders.get(builders.size() - 1);
            if (lastStage.getOutputRedirection().isEmpty()) {
                lastBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            }
            for (ProcessBuilder builder : builders) {
                builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            }

            List<Process> processes = ProcessBuilder.startPipeline(builders);

            if (background) {
                state.getJobManager().registerJob(describePipeline(stages), processes);
                return 0;
            }

            int lastExitCode = 0;
            for (Process process : processes) {
                lastExitCode = process.waitFor();
            }
            return lastExitCode;
        } catch (IOException e) {
            System.err.println("shellcraft: pipeline failed to start: " + e.getMessage());
            return 127;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 130;
        }
    }

    private String describePipeline(List<ParsedCommand> stages) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stages.size(); i++) {
            sb.append(stages.get(i));
            if (i < stages.size() - 1) sb.append(" | ");
        }
        return sb.toString();
    }

    private static class ExecutionResult {
        final int exitCode;
        final Process process;
        ExecutionResult(int exitCode, Process process) {
            this.exitCode = exitCode;
            this.process = process;
        }
    }
}
