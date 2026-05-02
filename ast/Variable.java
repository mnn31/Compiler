package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * A variable reference -- evaluates by looking up the name in the environment.
 *
 * @author Manan Gupta
 * @version 2026-05-02
 */
public class Variable extends Expression
{
    private final String name;

    /**
     * Returns this variable's identifier.
     *
     * @return the source-level variable name
     */
    public String getName()
    {
        return name;
    }

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

    /**
     * Loads the value at varname's address into $v0.
     *
     * @param e emitter to write MIPS to
     * @precondition e != null; the variable was emitted in the .data section
     * @postcondition $v0 holds the variable's current value
     */
    @Override
    public void compile(Emitter e)
    {
        e.emit("la $t0 var" + name);
        e.emit("lw $v0 ($t0)\t# load var" + name + " into $v0");
    }
}
