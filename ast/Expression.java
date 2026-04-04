package ast;

import environment.Environment;

/**
 * Abstract syntax tree node for an expression that evaluates to an integer value.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public abstract class Expression
{
    /**
     * Evaluates this expression using variable values from the environment.
     *
     * @param env the runtime environment holding variable bindings
     * @precondition env is non-null; variables referenced by this expression are defined
     * @postcondition the returned value equals this expression evaluated in env
     * @return the integer result of evaluation
     */
    public abstract int eval(Environment env);
}
