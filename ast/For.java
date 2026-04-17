package ast;

import environment.Environment;

/**
 * FOR loop (extra credit). Counts var from start TO end inclusive and runs body each time.
 * Supports BREAK and CONTINUE the same way WHILE does.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class For extends Statement
{
    private final String var;
    private final Expression start;
    private final Expression end;
    private final Statement body;

    /**
     * Sets up a FOR var := start TO end DO body node.
     *
     * @param var   name of the loop variable
     * @param start starting value (inclusive)
     * @param end   ending value (inclusive)
     * @param body  statement to execute each iteration
     * @precondition none of the params are null
     */
    public For(String var, Expression start, Expression end, Statement body)
    {
        this.var = var;
        this.start = start;
        this.end = end;
        this.body = body;
    }

    /**
     * Evaluates start and end once, then loops var from start to end.
     * BREAK exits early, CONTINUE moves to the next value of var.
     *
     * @param env environment where var is set and body runs
     * @precondition env != null
     */
    @Override
    public void exec(Environment env)
    {
        int from = start.eval(env);
        int to = end.eval(env);
        for (int i = from; i <= to; i++)
        {
            env.setVariable(var, i);
            try
            {
                body.exec(env);
            }
            catch (ContinueException expected)
            {
                // continue to next iteration
            }
            catch (BreakException e)
            {
                break;
            }
        }
    }
}
