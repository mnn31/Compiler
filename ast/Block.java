package ast;

import java.util.List;
import environment.Environment;

/**
 * Statement that runs a sequence of child statements in order, as in BEGIN ... END.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class Block extends Statement
{
    private final List<Statement> statements;

    /**
     * Constructs a block containing the given statements in execution order.
     *
     * @param statements the list of statements to run sequentially
     * @precondition statements is non-null
     * @postcondition this block holds that list in order
     */
    public Block(List<Statement> statements)
    {
        this.statements = statements;
    }

    /**
     * Runs each child statement in list order.
     *
     * @param env the runtime environment shared by all child statements
     * @precondition env is non-null
     * @postcondition each child has been executed in sequence unless one escapes with an
     *                exception
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
