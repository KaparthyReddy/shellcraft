package com.shellcraft.execution;

import com.shellcraft.core.ParsedCommand;
import com.shellcraft.core.Redirection;

import java.io.File;
import java.io.IOException;

/**
 * Builds a real OS process (via ProcessBuilder) for one command in a
 * pipeline, wiring up file-based redirections directly. Pipe-based
 * stdout->stdin connections between pipeline stages are handled by
 * ShellExecutor using ProcessBuilder's own Redirect.PIPE mechanism,
 * since that's the correct, efficient way to chain real OS processes -
 * manually shuttling bytes between them in Java would be both slower
 * and a reinvention of what the OS already does natively.
 */
public class ExternalProcessRunner {

    public ProcessBuilder buildProcessBuilder(ParsedCommand command, File workingDirectory) {
        ProcessBuilder builder = new ProcessBuilder(command.toFullCommandLine());
        builder.directory(workingDirectory);

        command.getInputRedirection().ifPresent(redirection ->
                builder.redirectInput(new File(redirection.getTargetFile())));

        command.getOutputRedirection().ifPresent(redirection -> {
            File targetFile = new File(redirection.getTargetFile());
            if (redirection.getType() == Redirection.Type.OUTPUT_APPEND) {
                builder.redirectOutput(ProcessBuilder.Redirect.appendTo(targetFile));
            } else {
                builder.redirectOutput(ProcessBuilder.Redirect.to(targetFile));
            }
        });

        return builder;
    }
}
