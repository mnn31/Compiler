package ast;

import environment.Environment;

/**
 * EXTRA! Counted FOR loop with inclusive start and end values for the loop variable.
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
     * Constructs a FOR loop from start through end inclusive.
     *
     * @param var   the loop variable name, updated before each body execution
     * @param start expression for the first value of the loop variable
     * @param end   expression for the last value of the loop variable
     * @param body  the statement executed once per value in the range
     * @precondition var, start, end, and body are non-null
     * @postcondition this node stores the loop parameters and body
     */
    public For(String var, Expression start, Expression end, Statement body)
    {
        this.var = var;
        this.start = start;
        this.end = end;
        this.body = body;
    }

    /**
     * Iterates the loop variable from the start through end value inclusive and runs the body.
     *
     * @param env the runtime environment holding the loop variable and other state
     * @precondition env is non-null; bounds evaluate in env
     * @postcondition loop variable runs from start through end unless break or continue applies
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
