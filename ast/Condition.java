package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Represents a boolean condition like "x > 5" or "a &lt;&gt; b".
 * Used by IF, WHILE, and REPEAT-UNTIL. Supports all 6 relational operators.
 *
 * @author Manan Gupta
 * @version 2026-05-02
 */
public class Condition
{
    private final Expression left;
    private final String relop;
    private final Expression right;

    /**
     * Creates a condition node.
     *
     * @param left  left side of the comparison
     * @param relop the relational operator (=, <>, <, >, <=, >=)
     * @param right right side of the comparison
     * @precondition all three params are non-null; relop is one of the 6 valid operators
     */
    public Condition(Expression left, String relop, Expression right)
    {
        this.left = left;
        this.relop = relop;
        this.right = right;
    }

    /**
     * Evaluates both sides and compares them with relop.
     *
     * @param env the environment for evaluating left and right
     * @return true if the condition holds, false otherwise
     * @throws IllegalStateException if somehow relop isn't one of the six valid operators
     */
    public boolean eval(Environment env)
    {
        int ll = left.eval(env);
        int rr = right.eval(env);
        boolean result;
        if (relop.equals("="))
        {
            result = ll == rr;
        }
        else if (relop.equals("<>"))
        {
            result = ll != rr;
        }
        else if (relop.equals("<"))
        {
            result = ll < rr;
        }
        else if (relop.equals(">"))
        {
            result = ll > rr;
        }
        else if (relop.equals("<="))
        {
            result = ll <= rr;
        }
        else if (relop.equals(">="))
        {
            result = ll >= rr;
        }
        else
        {
            throw new IllegalStateException("unrecognized relop '" + relop + "' -- parser bug?");
        }
        return result;
    }

    /**
     * Emits code that branches to targetLabel when the condition is FALSE.
     * The straight-line fallthrough therefore corresponds to the condition
     * being true, which is what IF/WHILE/REPEAT need. Left lands in $t0 and
     * right in $v0 before the branch.
     *
     * @param e emitter to write MIPS to
     * @param targetLabel label to branch to when this condition is false
     * @precondition e != null, targetLabel != null; relop is one of the six valid ones
     * @postcondition a branch instruction has been emitted; $t0 and $v0 are clobbered
     */
    public void compile(Emitter e, String targetLabel)
    {
        left.compile(e);
        e.emitPush("$v0");
        right.compile(e);
        e.emitPop("$t0");
        String branch;
        if (relop.equals("="))
        {
            branch = "bne";
        }
        else if (relop.equals("<>"))
        {
            branch = "beq";
        }
        else if (relop.equals("<"))
        {
            branch = "bge";
        }
        else if (relop.equals(">"))
        {
            branch = "ble";
        }
        else if (relop.equals("<="))
        {
            branch = "bgt";
        }
        else if (relop.equals(">="))
        {
            branch = "blt";
        }
        else
        {
            throw new IllegalStateException("unrecognized relop '" + relop + "' -- parser bug?");
        }
        e.emit(branch + " $t0 $v0 " + targetLabel);
    }
}
