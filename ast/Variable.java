package ast;

import environment.Environment;

/**
 * A variable reference -- evaluates by looking up the name in the environment.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class Variable extends Expression
{
    private final String name;

    /**
     * Creates a variable node for the given identifier name.
     *
     * @param name the variable's identifier
     * @precondition name is non-null and is a valid identifier
     * @postcondition this.name == name
     */
    public Variable(String name)
    {
        this.name = name;
    }

    /**
     * Looks up name in env and returns its value.
     * Will throw if the variable was never assigned.
     *
     * @param env the environment to search
     * @return current value of the variable
     * @precondition the variable has been set before this is called
     */
    @Override
    public int eval(Environment env)
    {
        return env.getVariable(name);
    }
}
