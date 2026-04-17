package ast;

import environment.Environment;

/**
 * IF statement, with an optional ELSE branch. elseStmt is null when there's no ELSE.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class If extends Statement
{
    private final Condition cond;
    private final Statement thenStmt;
    private final Statement elseStmt;

    /**
     * Creates an IF (THEN) (ELSE) node. Pass null for elseStmt if there's no else.
     *
     * @param cond     the condition to test
     * @param thenStmt statement to run when cond is true
     * @param elseStmt statement to run when cond is false, or null
     * @precondition cond != null, thenStmt != null
     * @postcondition this node stores all three fields
     */
    public If(Condition cond, Statement thenStmt, Statement elseStmt)
    {
        this.cond = cond;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    /**
     * Runs thenStmt if cond is true, elseStmt otherwise (if it exists).
     *
     * @param env the environment for evaluating the condition and running branches
     * @precondition env != null
     */
    @Override
    public void exec(Environment env)
    {
        if (cond.eval(env))
        {
            thenStmt.exec(env);
        }
        else if (elseStmt != null)
        {
            elseStmt.exec(env);
        }
    }
}
