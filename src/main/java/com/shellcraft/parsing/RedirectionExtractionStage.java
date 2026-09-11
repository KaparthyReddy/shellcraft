package com.shellcraft.parsing;

import com.shellcraft.core.ParsedCommand;
import com.shellcraft.core.Redirection;

import java.util.ArrayList;
import java.util.List;

/**
 * For each pipeline segment, pulls out redirection operators (>, >>, <)
 * and their target filenames, leaving a clean argv (program name +
 * arguments) with no shell syntax mixed in.
 */
public class RedirectionExtractionStage extends ParseStage {

    @Override
    protected void handle(ParseContext context) {
        List<ParsedCommand> commands = new ArrayList<>();

        for (List<Token> segment : context.getSegments()) {
            commands.add(extractFromSegment(segment));
        }

        context.setParsedCommands(commands);
    }

    private ParsedCommand extractFromSegment(List<Token> segment) {
        List<String> argv = new ArrayList<>();
        List<Redirection> redirections = new ArrayList<>();

        int i = 0;
        while (i < segment.size()) {
            String value = segment.get(i).getValue();

            Redirection.Type type = switch (value) {
                case ">" -> Redirection.Type.OUTPUT_TRUNCATE;
                case ">>" -> Redirection.Type.OUTPUT_APPEND;
                case "<" -> Redirection.Type.INPUT;
                default -> null;
            };

            if (type != null) {
                if (i + 1 >= segment.size()) {
                    throw new IllegalArgumentException("Redirection '" + value + "' missing target filename");
                }
                redirections.add(new Redirection(type, segment.get(i + 1).getValue()));
                i += 2;
            } else {
                argv.add(value);
                i++;
            }
        }

        if (argv.isEmpty()) {
            throw new IllegalArgumentException("Empty command segment");
        }

        String programName = argv.get(0);
        List<String> arguments = argv.subList(1, argv.size());
        return new ParsedCommand(programName, arguments, redirections);
    }
}
