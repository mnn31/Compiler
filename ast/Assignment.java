package ast;

import environment.Environment;

/**
 * Statement that evaluates an expression and stores the result in a named variable.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class Assignment extends Statement
{
    private final String var;
    private final Expression exp;

    /**
     * Constructs an assignment to the given variable.
     *
     * @param var the target variable name
     * @param exp the expression whose value is assigned
     * @precondition var and exp are non-null
     * @postcondition this assignment stores the variable name and expression
     */
    public Assignment(String var, Expression exp)
    {
        this.var = var;
        this.exp = exp;
    }

    /**
     * Evaluates the right-hand expression and assigns its value to the variable name.
     *
     * @param env the runtime environment updated with the new variable value
     * @precondition env is non-null; the expression can be evaluated in env
     * @postcondition the variable is bound to the evaluated value in env
     */
    @Override
    public void exec(Environment env)
    {
        env.setVariable(var, exp.eval(env));
    }
}
