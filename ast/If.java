package ast;

import environment.Environment;

/**
 * Conditional statement with a required then-branch and an optional else-branch.
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
     * Constructs an IF statement with optional ELSE.
     *
     * @param cond      the boolean condition controlling which branch runs
     * @param thenStmt  the statement executed when the condition is true
     * @param elseStmt  the statement executed when false, or null if there is no ELSE branch
     * @precondition cond and thenStmt are non-null
     * @postcondition this node stores the condition and branches
     */
    public If(Condition cond, Statement thenStmt, Statement elseStmt)
    {
        this.cond = cond;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    /**
     * Evaluates the condition and runs the then branch, or the else branch when present.
     *
     * @param env the runtime environment passed to the chosen branch
     * @precondition env is non-null; the condition can be evaluated in env
     * @postcondition exactly one branch runs (then, or else if present and condition false)
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
