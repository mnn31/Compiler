package ast;

/**
 * Thrown when a BreakStmt executes; caught by the innermost enclosing loop to
 * terminate that loop immediately.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class BreakException extends LoopControlException
{
    private static final long serialVersionUID = 1L;
}
