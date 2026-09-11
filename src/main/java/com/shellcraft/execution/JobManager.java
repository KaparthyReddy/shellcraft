package com.shellcraft.execution;

import com.shellcraft.core.JobStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tracks background jobs. When a pipeline runs with "&", a background
 * thread waits on the underlying OS process(es) and updates the job's
 * status to DONE/FAILED once they exit - the shell prompt itself is
 * never blocked waiting for a background job.
 */
public class JobManager {

    private final List<JobStatus> jobs = new ArrayList<>();
    private final AtomicInteger nextJobId = new AtomicInteger(1);

    public JobStatus registerJob(String commandLine, List<Process> processes) {
        JobStatus job = new JobStatus(nextJobId.getAndIncrement(), commandLine);
        jobs.add(job);

        Thread watcher = new Thread(() -> {
            try {
                for (Process process : processes) {
                    process.waitFor();
                }
                boolean allSucceeded = processes.stream().allMatch(p -> p.exitValue() == 0);
                job.setState(allSucceeded ? JobStatus.State.DONE : JobStatus.State.FAILED);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                job.setState(JobStatus.State.FAILED);
            }
        });
        watcher.setDaemon(true);
        watcher.start();

        return job;
    }

    public List<JobStatus> getAllJobs() {
        return List.copyOf(jobs);
    }
}
