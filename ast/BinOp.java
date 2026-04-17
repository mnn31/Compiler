package ast;

import environment.Environment;

/**
 * Binary arithmetic operation (+, -, *, /, mod) on two sub-expressions.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class BinOp extends Expression
{
    private final String op;
    private final Expression left;
    private final Expression right;

    /**
     * Builds a BinOp node.
     *
     * @param op    the operator string -- should be +, -, *, /, or mod
     * @param left  left-hand side
     * @param right right-hand side
     * @precondition none of the arguments are null
     */
    public BinOp(String op, Expression left, Expression right)
    {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    /**
     * Evaluates left and right, then applies op.
     * Note: integer division truncates (Java default behavior).
     *
     * @param env passed down to both sub-expressions
     * @return result of applying op to the two evaluated operands
     * @precondition env != null
     * @postcondition env is unchanged
     */
    @Override
    public int eval(Environment env)
    {
        int ll = left.eval(env);
        int rr = right.eval(env);
        if (op.equals("+"))
        {
            return ll + rr;
        }
        if (op.equals("-"))
        {
            return ll - rr;
        }
        if (op.equals("*"))
        {
            return ll * rr;
        }
        if (op.equals("/"))
        {
            return ll / rr;
        }
        if (op.equals("mod"))
        {
            return ll % rr;
        }
        throw new IllegalStateException("how did we get here? unknown op: " + op);
    }
}
