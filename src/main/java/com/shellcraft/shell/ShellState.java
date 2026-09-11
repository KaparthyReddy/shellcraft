package com.shellcraft.shell;

import com.shellcraft.execution.JobManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All mutable state a running shell session carries: current directory,
 * environment variables, command history, background jobs, and whether
 * an exit has been requested. Passed to every ShellCommand so built-ins
 * can read/mutate it without the shell needing bespoke plumbing per command.
 */
public class ShellState {

    private File currentDirectory;
    private final Map<String, String> environment;
    private final List<String> history = new ArrayList<>();
    private final JobManager jobManager = new JobManager();
    private volatile boolean exitRequested = false;

    public ShellState() {
        this.currentDirectory = new File(System.getProperty("user.dir"));
        this.environment = new HashMap<>(System.getenv());
    }

    public File getCurrentDirectory() { return currentDirectory; }
    public void setCurrentDirectory(File currentDirectory) { this.currentDirectory = currentDirectory; }

    public Map<String, String> getEnvironment() { return environment; }

    public void addToHistory(String commandLine) { history.add(commandLine); }
    public List<String> getHistory() { return history; }

    public JobManager getJobManager() { return jobManager; }

    public void requestExit() { this.exitRequested = true; }
    public boolean isExitRequested() { return exitRequested; }
}
