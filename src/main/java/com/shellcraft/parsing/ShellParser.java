package com.shellcraft.parsing;

import com.shellcraft.core.Pipeline;

import java.util.Map;

/**
 * Builds and drives the parsing chain: Tokenizer -> VariableExpansion ->
 * PipelineSplitter -> RedirectionExtraction. Callers only ever interact
 * with this class - the chain's internal stages are an implementation
 * detail.
 */
public class ShellParser {

    private final ParseStage chainHead;

    public ShellParser() {
        ParseStage tokenizer = new TokenizerStage();
        ParseStage variableExpansion = new VariableExpansionStage();
        ParseStage pipelineSplitter = new PipelineSplitterStage();
        ParseStage redirectionExtraction = new RedirectionExtractionStage();

        tokenizer.setNext(variableExpansion)
                 .setNext(pipelineSplitter)
                 .setNext(redirectionExtraction);

        this.chainHead = tokenizer;
    }

    public Pipeline parse(String input, Map<String, String> environment) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Cannot parse an empty command line");
        }

        ParseContext context = new ParseContext(input, environment);
        chainHead.process(context);
        return context.toPipeline();
    }
}
