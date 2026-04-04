package ast;

/**
 * EXTRA! Thrown when a ContinueStmt executes; caught by the innermost enclosing loop to
 * skip the rest of the current iteration.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class ContinueException extends LoopControlException
{
    private static final long serialVersionUID = 1L;
}
