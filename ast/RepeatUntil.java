package ast;

import environment.Environment;

/**
 * REPEAT...UNTIL loop (extra credit). Like a do-while -- body always runs at least once,
 * and keeps going until the condition is true (opposite of WHILE).
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class RepeatUntil extends Statement
{
    private final Statement body;
    private final Condition cond;

    /**
     * Creates a REPEAT body UNTIL cond node.
     *
     * @param body the loop body -- runs at least once no matter what
     * @param cond stop condition -- loop ends when this is true
     * @precondition body != null, cond != null
     */
    public RepeatUntil(Statement body, Condition cond)
    {
        this.body = body;
        this.cond = cond;
    }

    /**
     * Runs body, checks cond, repeats if cond is still false.
     * BREAK exits immediately, CONTINUE skips to the condition check.
     *
     * @param env the environment for body and condition
     * @precondition env != null
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
            catch (ContinueException expected)
            {
                // continue to next iteration
            }
            catch (BreakException e)
            {
                return;
            }
        }
        while (!cond.eval(env));
    }
}
