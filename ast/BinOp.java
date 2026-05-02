package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Binary arithmetic operation (+, -, *, /, mod) on two sub-expressions.
 *
 * @author Manan Gupta
 * @version 2026-05-02
 */
public class BinOp extends Expression
{
    private final String op;
    private final Expression left;
    private final Expression right;

    /**
     * Builds a BinOp node.
     *
     * @param op    the operator string -- should be +, -, *, /, or mod
     * @param left  left-hand side
     * @param right right-hand side
     * @precondition none of the arguments are null
     */
    public BinOp(String op, Expression left, Expression right)
    {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    /**
     * Evaluates left and right, then applies op.
     * Note: integer division truncates (Java default behavior).
     *
     * @param env passed down to both sub-expressions
     * @return result of applying op to the two evaluated operands
     * @precondition env != null
     * @postcondition env is unchanged
     */
    @Override
    public int eval(Environment env)
    {
        int ll = left.eval(env);
        int rr = right.eval(env);
        if (op.equals("+"))
        {
            return ll + rr;
        }
        if (op.equals("-"))
        {
            return ll - rr;
        }
        if (op.equals("*"))
        {
            return ll * rr;
        }
        if (op.equals("/"))
        {
            return ll / rr;
        }
        if (op.equals("mod"))
        {
            return ll % rr;
        }
        throw new IllegalStateException("how did we get here? unknown op: " + op);
    }

    /**
     * Compiles left, pushes its $v0 onto the stack, compiles right, pops the
     * stored left into $t0, then applies op leaving the result in $v0. For
     * "/" and "mod" the result word is taken from LO (mflo).
     *
     * @param e emitter to write MIPS to
     * @precondition e != null; op is one of +, -, *, /, mod
     * @postcondition $v0 holds left op right; the stack pointer is unchanged
     */
    @Override
    public void compile(Emitter e)
    {
        left.compile(e);
        e.emitPush("$v0");
        right.compile(e);
        e.emitPop("$t0");
        if (op.equals("+"))
        {
            e.emit("addu $v0 $t0 $v0");
        }
        else if (op.equals("-"))
        {
            e.emit("subu $v0 $t0 $v0");
        }
        else if (op.equals("*"))
        {
            e.emit("mult $t0 $v0");
            e.emit("mflo $v0");
        }
        else if (op.equals("/"))
        {
            e.emit("div $t0 $v0");
            e.emit("mflo $v0");
        }
        else if (op.equals("mod"))
        {
            e.emit("div $t0 $v0");
            e.emit("mfhi $v0");
        }
        else
        {
            throw new IllegalStateException("unknown op for compile: " + op);
        }
    }
}
