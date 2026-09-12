package com.shellcraft.execution;

import com.shellcraft.core.ParsedCommand;
import com.shellcraft.core.Redirection;

import java.io.File;

public class ExternalProcessRunner {

    public ProcessBuilder buildProcessBuilder(ParsedCommand command, File workingDirectory) {
        ProcessBuilder builder = new ProcessBuilder(command.toFullCommandLine());
        builder.directory(workingDirectory);

        command.getInputRedirection().ifPresent(redirection ->
                builder.redirectInput(resolveAgainstWorkingDir(redirection.getTargetFile(), workingDirectory)));

        command.getOutputRedirection().ifPresent(redirection -> {
            File targetFile = resolveAgainstWorkingDir(redirection.getTargetFile(), workingDirectory);
            if (redirection.getType() == Redirection.Type.OUTPUT_APPEND) {
                builder.redirectOutput(ProcessBuilder.Redirect.appendTo(targetFile));
            } else {
                builder.redirectOutput(ProcessBuilder.Redirect.to(targetFile));
            }
        });

        return builder;
    }

    /** Resolves a redirection target against the shell's current directory,
     * not the JVM's own cwd - ProcessBuilder.directory() only affects the
     * child process's own path resolution, not File objects the parent
     * constructs for stream redirection. */
    private File resolveAgainstWorkingDir(String path, File workingDirectory) {
        File file = new File(path);
        return file.isAbsolute() ? file : new File(workingDirectory, path);
    }
}