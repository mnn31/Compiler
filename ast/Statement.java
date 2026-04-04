package ast;

import environment.Environment;

/**
 * Abstract syntax tree node for a statement that may update the environment or perform I/O.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public abstract class Statement
{
    /**
     * Executes this statement, possibly modifying the environment or printing output.
     *
     * @param env the runtime environment for variables and side effects
     * @precondition env is non-null; any variables read are defined when required
     * @postcondition semantics are applied to env, or LoopControlException is used for
     *                break/continue control flow
     */
    public abstract void exec(Environment env);
}
