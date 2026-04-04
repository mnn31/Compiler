package ast;

import environment.Environment;

/**
 * AST node for a binary arithmetic operator on two subexpressions.
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
     * Constructs a binary operation with the given operator and operands.
     *
     * @param op    one of +, -, *, /, or mod
     * @param left  the left operand expression
     * @param right the right operand expression
     * @precondition op, left, and right are non-null
     * @postcondition this node holds the given operator and operands
     */
    public BinOp(String op, Expression left, Expression right)
    {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    /**
     * Evaluates both operands and applies the arithmetic or mod operator.
     *
     * @param env the runtime environment passed to subexpressions
     * @return the integer result of applying this operator to the operand values
     * @precondition env is non-null; operands evaluate in env
     * @postcondition returns the combined value per this operator
     */
    @Override
    public int eval(Environment env)
    {
        int l = left.eval(env);
        int r = right.eval(env);
        if (op.equals("+"))
        {
            return l + r;
        }
        if (op.equals("-"))
        {
            return l - r;
        }
        if (op.equals("*"))
        {
            return l * r;
        }
        if (op.equals("/"))
        {
            return l / r;
        }
        if (op.equals("mod"))
        {
            return l % r;
        }
        throw new IllegalStateException("Unknown op: " + op);
    }
}
