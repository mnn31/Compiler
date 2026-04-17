package ast;

import environment.Environment;

/**
 * Base class for all statement nodes in the AST. Each subclass knows how to
 * execute itself given an environment.
 *
 * @author Manan Gupta
 * @version 2026-03-25
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
}
