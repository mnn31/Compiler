package ast;

import environment.Environment;

/**
 * AST node for a variable reference; evaluation looks up the name in the environment.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class Variable extends Expression
{
    private final String name;

    /**
     * Constructs a reference to the named variable.
     *
     * @param name the identifier of the variable to read
     * @precondition name is non-null
     * @postcondition this node refers to that identifier
     */
    public Variable(String name)
    {
        this.name = name;
    }

    /**
     * Looks up this variable name in the environment.
     *
     * @param env the runtime environment holding current variable values
     * @return the value bound to this variable name
     * @precondition env is non-null; the variable is defined in env
     * @postcondition returns the stored value for this name
     */
    @Override
    public int eval(Environment env)
    {
        return env.getVariable(name);
    }
}
