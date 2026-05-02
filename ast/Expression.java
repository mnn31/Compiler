package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Base class for expression nodes -- anything that evaluates to an int.
 *
 * @author Manan Gupta
 * @version 2026-05-02
 */
public abstract class Expression
{
    /**
     * Evaluates this expression and returns the result.
     *
     * @param env environment to look up variables in
     * @return the integer value this expression evaluates to
     * @precondition env != null, all referenced variables are defined
     * @postcondition env is not modified
     */
    public abstract int eval(Environment env);

    /**
     * Default compile method, overridden by every concrete expression. The
     * default throws so a missing override surfaces as a clear runtime error.
     *
     * @param e emitter to write MIPS to
     * @precondition e != null
     * @postcondition the expression's value is left in $v0
     */
    public void compile(Emitter e)
    {
        throw new RuntimeException("Implement me!!!!!");
    }
}
