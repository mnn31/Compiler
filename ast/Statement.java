package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Base class for all statement nodes in the AST. Each subclass knows how to
 * execute itself given an environment.
 *
 * @author Manan Gupta
 * @version 2026-05-02
 */
public abstract class Statement
{
    /**
     * Runs this statement in the given environment.
     *
     * @param env the environment holding all current variable values
     * @precondition env != null
     */
    public abstract void exec(Environment env);

    /**
     * Default compile method, overridden by every concrete statement. The
     * default throws so a missing override surfaces as a clear runtime error.
     *
     * @param e emitter to write MIPS to
     * @precondition e != null
     * @postcondition MIPS instructions for this statement have been emitted
     */
    public void compile(Emitter e)
    {
        throw new RuntimeException("Implement me!!!!!");
    }
}
