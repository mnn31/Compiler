package ast;

import environment.Environment;

/**
 * Base class for expression nodes -- anything that evaluates to an int.
 *
 * @author Manan Gupta
 * @version 2026-03-25
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
}
