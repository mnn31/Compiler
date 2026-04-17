package ast;

import environment.Environment;

/**
 * Handles variable assignment (the := operator). Evaluates the right-hand side
 * and stores the result in the environment under the given variable name.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class Assignment extends Statement
{
    private final String var;
    private final Expression exp;

    /**
     * Creates an assignment node for "var := exp".
     *
     * @param var target variable name
     * @param exp expression on the right side of :=
     * @precondition var != null, exp != null
     * @postcondition stores var and exp for later execution
     */
    public Assignment(String var, Expression exp)
    {
        this.var = var;
        this.exp = exp;
    }

    /**
     * Evaluates exp and stores the result under var in env.
     *
     * @param env the environment to update
     * @precondition env != null
     * @postcondition env.getVariable(var) returns the evaluated result
     */
    @Override
    public void exec(Environment env)
    {
        env.setVariable(var, exp.eval(env));
    }
}
