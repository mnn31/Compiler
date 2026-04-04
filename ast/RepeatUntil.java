package ast;

import environment.Environment;

/**
 * EXTRA! Post-test loop: runs the body at least once, then repeats until the condition becomes true.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class RepeatUntil extends Statement
{
    private final Statement body;
    private final Condition cond;

    /**
     * Constructs a REPEAT-UNTIL loop.
     *
     * @param body the statement run each iteration until termination
     * @param cond the condition; the loop stops when this evaluates to true
     * @precondition body and cond are non-null
     * @postcondition this node stores the body and termination condition
     */
    public RepeatUntil(Statement body, Condition cond)
    {
        this.body = body;
        this.cond = cond;
    }

    /**
     * Runs the body at least once, then repeats until the condition becomes true.
     *
     * @param env the runtime environment for the body and condition
     * @precondition env is non-null
     * @postcondition stops when cond is true or the body exits with break
     */
    @Override
    public void exec(Environment env)
    {
        do
        {
            try
            {
                body.exec(env);
            }
            catch (ContinueException e)
            {
                // continue repeat loop
            }
            catch (BreakException e)
            {
                return;
            }
        }
        while (!cond.eval(env));
    }
}
