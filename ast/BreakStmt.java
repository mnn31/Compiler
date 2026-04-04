package ast;

import environment.Environment;

/**
 * Statement that exits the innermost enclosing loop by throwing BreakException.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class BreakStmt extends Statement
{
    /**
     * Throws BreakException so the innermost enclosing loop can exit.
     *
     * @param env the runtime environment (unused)
     * @precondition none
     * @postcondition does not return; throws BreakException
     */
    @Override
    public void exec(Environment env)
    {
        throw new BreakException();
    }
}
