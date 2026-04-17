package ast;

import environment.Environment;

/**
 * A number literal node -- just wraps a single int. Simplest AST node there is.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class Number extends Expression
{
    private final int value;

    /**
     * Stores the given integer constant.
     *
     * @param value the literal integer value
     */
    public Number(int value)
    {
        this.value = value;
    }

    /**
     * Returns the stored value. env is ignored since there's nothing to look up.
     *
     * @param env not used
     * @return the integer constant
     */
    @Override
    public int eval(Environment env)
    {
        return value;
    }
}
