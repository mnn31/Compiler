package ast;

import emitter.Emitter;
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

    /**
     * Compiles the FOR loop to MIPS. Initializes var to start, then at the top
     * of the loop it pushes the current var value, evaluates end into $v0,
     * pops the saved var into $t0, and branches out if $t0 > $v0. After the
     * body it increments var and jumps back to the top label.
     *
     * @param e emitter to write MIPS to
     * @precondition e != null; var was emitted in the .data section
     * @postcondition body runs once for every value of var from start to end
     */
    @Override
    public void compile(Emitter e)
    {
        int id = e.nextLabelID();
        String topLabel = "for" + id;
        String endLabel = "endfor" + id;
        start.compile(e);
        e.emit("la $t0 var" + var);
        e.emit("sw $v0 ($t0)\t# init " + var + " for FOR loop");
        e.emit(topLabel + ":");
        e.emit("la $t0 var" + var);
        e.emit("lw $v0 ($t0)");
        e.emitPush("$v0");
        end.compile(e);
        e.emitPop("$t0");
        e.emit("bgt $t0 $v0 " + endLabel);
        body.compile(e);
        e.emit("la $t0 var" + var);
        e.emit("lw $v0 ($t0)");
        e.emit("addu $v0 $v0 1\t# bump " + var);
        e.emit("sw $v0 ($t0)");
        e.emit("j " + topLabel);
        e.emit(endLabel + ":");
    }
}
