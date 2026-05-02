package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Handles variable assignment (the := operator). Evaluates the right-hand side
 * and stores the result in the environment under the given variable name.
 *
 * @author Manan Gupta
 * @version 2026-05-02
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

    /**
     * Compiles exp into $v0, then stores $v0 to the variable's address in .data.
     *
     * @param e emitter to write MIPS to
     * @precondition e != null; var was emitted in the .data section
     * @postcondition the variable now holds the value of exp at runtime
     */
    @Override
    public void compile(Emitter e)
    {
        exp.compile(e);
        e.emit("la $t0 var" + var);
        e.emit("sw $v0 ($t0)\t# store $v0 into var" + var);
    }
}
