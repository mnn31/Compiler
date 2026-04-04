package ast;

import environment.Environment;

/**
 * EXTRA! Statement that skips to the next iteration of the innermost loop by throwing
 * ContinueException.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class ContinueStmt extends Statement
{
    /**
     * Throws ContinueException so the innermost enclosing loop can skip the rest of the body.
     *
     * @param env the runtime environment (unused)
     * @precondition none
     * @postcondition does not return; throws ContinueException
     */
    @Override
    public void exec(Environment env)
    {
        throw new ContinueException();
    }
}
