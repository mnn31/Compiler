package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * A number literal node -- just wraps a single int. Simplest AST node there is.
 *
 * @author Manan Gupta
 * @version 2026-05-02
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

    /**
     * Loads the literal into $v0.
     *
     * @param e emitter to write MIPS to
     * @precondition e != null
     * @postcondition $v0 holds value
     */
    @Override
    public void compile(Emitter e)
    {
        e.emit("li $v0 " + value);
    }
}
