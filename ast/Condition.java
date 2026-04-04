package ast;

import environment.Environment;

/**
 * Boolean condition formed by comparing two integer expressions with a relational operator.
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
     * Constructs a condition comparing two expressions.
     *
     * @param left  the left-hand side expression
     * @param relop one of =, <>, <, >, <=, or >=
     * @param right the right-hand side expression
     * @precondition left and right are non-null; relop is a supported operator string
     * @postcondition this condition holds the given operands and operator
     */
    public Condition(Expression left, String relop, Expression right)
    {
        this.left = left;
        this.relop = relop;
        this.right = right;
    }

    /**
     * Evaluates both sides and tests the relation.
     *
     * @param env the runtime environment for evaluating subexpressions
     * @return true if the relation holds between the evaluated values
     * @precondition env is non-null; subexpressions can be evaluated in env
     * @postcondition returns whether the relation holds; does not change env bindings
     * @throws IllegalStateException if the relational operator is not recognized
     */
    public boolean eval(Environment env)
    {
        int l = left.eval(env);
        int r = right.eval(env);
        boolean result;
        if (relop.equals("="))
        {
            result = l == r;
        }
        else if (relop.equals("<>"))
        {
            result = l != r;
        }
        else if (relop.equals("<"))
        {
            result = l < r;
        }
        else if (relop.equals(">"))
        {
            result = l > r;
        }
        else if (relop.equals("<="))
        {
            result = l <= r;
        }
        else if (relop.equals(">="))
        {
            result = l >= r;
        }
        else
        {
            throw new IllegalStateException("Unknown relop: " + relop);
        }
        return result;
    }
}
