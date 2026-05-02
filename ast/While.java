package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * WHILE loop -- checks condition before each iteration.
 * BREAK exits immediately, CONTINUE skips to the next check.
 *
 * @author Manan Gupta
 * @version 2026-05-02
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

    /**
     * Compiles the loop with a top label that re-checks cond and a bottom
     * label that the false branch jumps to. The body unconditionally jumps
     * back to the top label after each iteration.
     *
     * @param e emitter to write MIPS to
     * @precondition e != null
     * @postcondition the body runs zero or more times until cond is false
     */
    @Override
    public void compile(Emitter e)
    {
        int id = e.nextLabelID();
        String topLabel = "while" + id;
        String endLabel = "endwhile" + id;
        e.emit(topLabel + ":");
        cond.compile(e, endLabel);
        body.compile(e);
        e.emit("j " + topLabel);
        e.emit(endLabel + ":");
    }
}
