package ast;

import java.util.List;
import environment.Environment;

/**
 * A BEGIN...END block. Just runs a list of statements in order.
 * Also used as the top-level container for a whole program.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class Block extends Statement
{
    private final List<Statement> statements;

    /**
     * Wraps a list of statements into a block.
     *
     * @param statements ordered list of statements to execute
     * @precondition statements != null (can be empty though)
     */
    public Block(List<Statement> statements)
    {
        this.statements = statements;
    }

    /**
     * Executes each statement in order. If one throws a BreakException or
     * ContinueException, it propagates up to the nearest enclosing loop.
     *
     * @param env shared environment for all statements in this block
     * @precondition env != null
     */
    @Override
    public void exec(Environment env)
    {
        for (Statement s : statements)
        {
            s.exec(env);
        }
    }
}
