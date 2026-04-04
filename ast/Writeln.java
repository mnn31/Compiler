package ast;

import environment.Environment;

/**
 * Statement that evaluates an expression and prints its value on a line to standard output.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class Writeln extends Statement
{
    private final Expression exp;

    /**
     * Constructs a WRITELN statement for the given expression.
     *
     * @param exp the expression whose value is printed when executed
     * @precondition exp is non-null
     * @postcondition this node holds the expression to print
     */
    public Writeln(Expression exp)
    {
        this.exp = exp;
    }

    /**
     * Prints the value of the expression on its own line.
     *
     * @param env the runtime environment used to evaluate the expression
     * @precondition env is non-null; the expression evaluates in env
     * @postcondition one line of output is printed to standard output
     */
    @Override
    public void exec(Environment env)
    {
        System.out.println(exp.eval(env));
    }
}
