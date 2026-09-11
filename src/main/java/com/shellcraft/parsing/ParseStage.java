package com.shellcraft.parsing;

/**
 * A single link in the parsing chain. Each stage performs its own
 * concern on the shared ParseContext, then delegates to the next stage -
 * classic Chain of Responsibility, except every stage handles the
 * request in part rather than one stage claiming it exclusively, since
 * parsing genuinely is a sequence of cumulative transformations.
 */
public abstract class ParseStage {

    private ParseStage next;

    public ParseStage setNext(ParseStage next) {
        this.next = next;
        return next; // allows fluent chaining: a.setNext(b).setNext(c)
    }

    public final void process(ParseContext context) {
        handle(context);
        if (next != null) {
            next.process(context);
        }
    }

    /** Subclasses implement their specific parsing concern here. */
    protected abstract void handle(ParseContext context);
}
