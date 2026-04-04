package ast;

import environment.Environment;

/**
 * Loop that re-evaluates the condition before each iteration and supports break and continue.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class While extends Statement
{
    private final Condition cond;
    private final Statement body;

    /**
     * Constructs a WHILE loop with the given guard and body.
     *
     * @param cond the condition tested before each iteration
     * @param body the statement executed for each iteration while the condition holds
     * @precondition cond and body are non-null
     * @postcondition this node stores the guard and body
     */
    public While(Condition cond, Statement body)
    {
        this.cond = cond;
        this.body = body;
    }

    /**
     * Repeats the body while the condition is true.
     *
     * @param env the runtime environment for the condition and body
     * @precondition env is non-null; condition and body can run in env
     * @postcondition loop exits when the condition is false or on break
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
            catch (ContinueException e)
            {
                // next iteration of the loop HERE!!!
            }
            catch (BreakException e)
            {
                break;
            }
        }
    }
}
