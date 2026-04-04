package ast;

/**
 * EXTRA! Base runtime exception for non-local control flow from BREAK and CONTINUE statements
 * inside loop bodies.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public abstract class LoopControlException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
}
