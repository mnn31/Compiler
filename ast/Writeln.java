package ast;

import environment.Environment;

/**
 * WRITELN statement -- evaluates the expression and prints it on its own line.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class Writeln extends Statement
{
    private final Expression exp;

    /**
     * Creates a WRITELN node for the given expression.
     *
     * @param exp expression to evaluate and print
     * @precondition exp != null
     */
    public Writeln(Expression exp)
    {
        this.exp = exp;
    }

    /**
     * Evaluates exp and prints the result with a newline.
     *
     * @param env environment to evaluate the expression in
     * @precondition env != null
     * @postcondition one integer line written to stdout
     */
    @Override
    public void exec(Environment env)
    {
        System.out.println(exp.eval(env));
    }
}
