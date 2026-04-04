package ast;

import environment.Environment;

/**
 * AST node representing an integer literal in the source program.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class Number extends Expression
{
    private final int value;

    /**
     * Constructs a literal expression with the given value.
     *
     * @param value the integer value of this literal
     * @precondition none
     * @postcondition this literal stores the given value
     */
    public Number(int value)
    {
        this.value = value;
    }

    /**
     * Returns this literal integer value; env is not used.
     *
     * @param env the runtime environment (unused for literals)
     * @return this node literal value
     * @precondition env may be any value; it is ignored
     * @postcondition returns the stored literal value
     */
    @Override
    public int eval(Environment env)
    {
        return value;
    }
}
