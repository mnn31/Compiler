package ast;

import environment.Environment;

/**
 * WHILE loop -- checks condition before each iteration.
 * BREAK exits immediately, CONTINUE skips to the next check.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class While extends Statement
{
    private final Condition cond;
    private final Statement body;

    /**
     * Creates a while loop node.
     *
     * @param cond the loop guard -- checked before every iteration
     * @param body what to run each time through
     * @precondition cond != null, body != null
     */
    public While(Condition cond, Statement body)
    {
        this.cond = cond;
        this.body = body;
    }

    /**
     * Runs body in a loop until cond is false or a BREAK is hit.
     * ContinueException just skips to re-checking the condition.
     *
     * @param env environment for condition checks and body execution
     * @precondition env != null
     */
    @Override
    public void exec(Environment env)
    {
        while (cond.eval(env))
        {
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
