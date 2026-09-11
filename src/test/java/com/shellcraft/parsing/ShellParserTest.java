package com.shellcraft.parsing;

import com.shellcraft.core.Pipeline;
import com.shellcraft.core.Redirection;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShellParserTest {

    private final ShellParser parser = new ShellParser();

    @Test
    void parsesSimpleCommand() {
        Pipeline pipeline = parser.parse("echo hello", Map.of());

        assertTrue(pipeline.isSingleCommand());
        assertEquals("echo", pipeline.getStages().get(0).getProgramName());
        assertEquals(1, pipeline.getStages().get(0).getArguments().size());
    }

    @Test
    void parsesMultiStagePipeline() {
        Pipeline pipeline = parser.parse("cat file.txt | grep foo | wc -l", Map.of());

        assertEquals(3, pipeline.getStages().size());
        assertEquals("cat", pipeline.getStages().get(0).getProgramName());
        assertEquals("grep", pipeline.getStages().get(1).getProgramName());
        assertEquals("wc", pipeline.getStages().get(2).getProgramName());
    }

    @Test
    void parsesOutputRedirection() {
        Pipeline pipeline = parser.parse("echo hi > out.txt", Map.of());

        var redirection = pipeline.getStages().get(0).getOutputRedirection().orElseThrow();
        assertEquals(Redirection.Type.OUTPUT_TRUNCATE, redirection.getType());
        assertEquals("out.txt", redirection.getTargetFile());
    }

    @Test
    void parsesBackgroundFlag() {
        Pipeline pipeline = parser.parse("sleep 10 &", Map.of());
        assertTrue(pipeline.isBackground());
    }

    @Test
    void expandsVariablesDuringParse() {
        Pipeline pipeline = parser.parse("echo $GREETING", Map.of("GREETING", "hello"));
        assertEquals("hello", pipeline.getStages().get(0).getArguments().get(0));
    }

    @Test
    void throwsOnEmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("", Map.of()));
    }

    @Test
    void throwsOnDanglingRedirection() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("echo hi >", Map.of()));
    }
}
