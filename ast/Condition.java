package ast;

import environment.Environment;

/**
 * Represents a boolean condition like "x > 5" or "a <> b".
 * Used by IF, WHILE, and REPEAT-UNTIL. Supports all 6 relational operators.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class Condition
{
    private final Expression left;
    private final String relop;
    private final Expression right;

    /**
     * Creates a condition node.
     *
     * @param left  left side of the comparison
     * @param relop the relational operator (=, <>, <, >, <=, >=)
     * @param right right side of the comparison
     * @precondition all three params are non-null; relop is one of the 6 valid operators
     */
    public Condition(Expression left, String relop, Expression right)
    {
        this.left = left;
        this.relop = relop;
        this.right = right;
    }

    /**
     * Evaluates both sides and compares them with relop.
     *
     * @param env the environment for evaluating left and right
     * @return true if the condition holds, false otherwise
     * @throws IllegalStateException if somehow relop isn't one of the six valid operators
     */
    public boolean eval(Environment env)
    {
        int ll = left.eval(env);
        int rr = right.eval(env);
        boolean result;
        if (relop.equals("="))
        {
            result = ll == rr;
        }
        else if (relop.equals("<>"))
        {
            result = ll != rr;
        }
        else if (relop.equals("<"))
        {
            result = ll < rr;
        }
        else if (relop.equals(">"))
        {
            result = ll > rr;
        }
        else if (relop.equals("<="))
        {
            result = ll <= rr;
        }
        else if (relop.equals(">="))
        {
            result = ll >= rr;
        }
        else
        {
            throw new IllegalStateException("unrecognized relop '" + relop + "' -- parser bug?");
        }
        return result;
    }
}
