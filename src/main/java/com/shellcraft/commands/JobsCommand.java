package com.shellcraft.commands;

import com.shellcraft.core.JobStatus;
import com.shellcraft.core.ParsedCommand;
import com.shellcraft.shell.ShellState;

public class JobsCommand implements ShellCommand {

    @Override
    public int execute(ParsedCommand command, ShellState state) {
        for (JobStatus job : state.getJobManager().getAllJobs()) {
            System.out.println(job);
        }
        return 0;
    }

    @Override
    public String getName() { return "jobs"; }
}
