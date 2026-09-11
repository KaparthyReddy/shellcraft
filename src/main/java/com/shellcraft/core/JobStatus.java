package com.shellcraft.core;

/** Tracks a background job: its shell-assigned ID, the underlying OS process(es), and its state. */
public final class JobStatus {

    public enum State { RUNNING, DONE, FAILED }

    private final int jobId;
    private final String commandLine;
    private volatile State state;

    public JobStatus(int jobId, String commandLine) {
        this.jobId = jobId;
        this.commandLine = commandLine;
        this.state = State.RUNNING;
    }

    public int getJobId() { return jobId; }
    public String getCommandLine() { return commandLine; }
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }

    @Override
    public String toString() {
        return String.format("[%d] %-8s %s", jobId, state, commandLine);
    }
}
