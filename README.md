# ShellCraft

A custom Unix-like shell interpreter supporting pipes, I/O redirection, and background job control — built-in commands implemented as Command pattern objects, parsing driven by Chain of Responsibility. Built to demonstrate real systems programming (genuine OS process control, real file descriptors, real pipes) underneath clean OOAD, distinct in scope from a scheduling-focused OS project (kthread).

## What this is

A shell's real complexity isn't string splitting — it's process control. This implementation uses Java's `ProcessBuilder` (including `ProcessBuilder.startPipeline`) to spawn genuine OS processes and wire them together with real OS-level pipes, not a simulation of them. Running `ls | grep foo > out.txt &` in this shell does exactly what it looks like: forks real processes, connects them with a real pipe, redirects real file descriptors, and backgrounds the job while the shell keeps accepting input.

## Design patterns

- **Command** (`ShellCommand`) — every built-in (`cd`, `pwd`, `exit`, `history`, `export`, `jobs`) implements the same interface, registered in `CommandRegistry`. Adding a new built-in means writing one class and one registration line — the parser and executor never change.
- **Chain of Responsibility** (`ParseStage`) — parsing is a pipeline of independent stages (tokenizer → variable expansion → pipeline splitter → redirection extraction), each handling one concern and passing the rest along, rather than one tangled parse function.

## Core features

- Built-in commands (in-process, Command pattern) vs. external programs (real `ProcessBuilder` exec)
- Multi-stage pipes: `cmd1 | cmd2 | cmd3` — real OS pipes, not Java-side byte-shuttling
- I/O redirection: `>`, `>>`, `<`
- Background jobs: `cmd &`, tracked and listed via `jobs`
- Environment variable expansion: `$HOME`, `$PATH`, with correct single-quote suppression (`'$HOME'` stays literal, matching real shell semantics)
- Command history

**Known limitation, stated plainly:** piping *from* a built-in mid-pipeline (e.g. `history | grep foo`) isn't supported — built-ins run in-process and don't have OS-level file descriptors to wire into a real pipe the way external processes do. Built-ins work fully as standalone commands.

## Core components

| Layer | What it does |
|---|---|
| `core/` | `ParsedCommand`, `Pipeline`, `Redirection`, `JobStatus` — the domain model |
| `parsing/` | The Chain of Responsibility: `TokenizerStage`, `VariableExpansionStage`, `PipelineSplitterStage`, `RedirectionExtractionStage`, driven by `ShellParser` |
| `commands/` | Six `ShellCommand` built-ins + `CommandRegistry` |
| `execution/` | `ShellExecutor` (dispatches built-in vs. external, wires real pipes), `ExternalProcessRunner` (builds `ProcessBuilder`s with correct redirection resolution), `JobManager` (tracks background jobs via a daemon watcher thread) |
| `shell/` | `ShellState` (mutable session state), `ShellSession` (the read-eval-print loop) |

## Tech stack

- Java 17, Maven
- JUnit 5
- GitHub Actions CI

## Project structure

```text
shellcraft/
├── src/main/java/com/shellcraft/
│ ├── core/
│ ├── parsing/
│ ├── commands/
│ ├── execution/
│ ├── shell/
│ └── Main.java
└── src/test/java/com/shellcraft/
├── parsing/
├── commands/
└── execution/
```


## Running it

```bash
mvn clean compile   # build
mvn test             # run the test suite (32 tests)
mvn compile exec:java # run the shell interactively
```

## Verified working example

A real interactive session, confirming genuine process control rather than simulated behavior:

```bash
shellcraft $ cd ..
kaparthyreddy $ cd shellcraft
shellcraft $ echo test > out.txt
shellcraft $ echo more >> out.txt
shellcraft $ cat out.txt
test
more
shellcraft $ ls | grep pom
pom.xml
shellcraft $ sleep 5 &
shellcraft $ jobs
[1] RUNNING sleep 5
```


`cd ..` resolves to a clean canonical path (not a literal `/path/..`). Redirection genuinely writes to real files, both truncating (`>`) and appending (`>>`). The pipe genuinely passes `ls`'s real stdout into `grep`'s real stdin — two independent OS processes communicating through a real kernel-level pipe. Background jobs run without blocking the prompt and are tracked correctly.

## Testing note

`ExternalProcessRunnerTest` and parts of `ShellExecutorTest` run against real OS commands (`echo`, `cat`) rather than mocking `ProcessBuilder` — forking real processes and wiring real file descriptors is the entire point of these classes, so testing them any other way would be testing something other than what they actually do.

## Test coverage

32 tests across parsing, commands, and execution — all passing.

```bash
Tests run: 32, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```


## License

MIT
