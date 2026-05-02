package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * IF statement, with an optional ELSE branch. elseStmt is null when there's no ELSE.
 *
 * @author Manan Gupta
 * @version 2026-05-02
 */
public class If extends Statement
{
    private final Condition cond;
    private final Statement thenStmt;
    private final Statement elseStmt;

    /**
     * Creates an IF (THEN) (ELSE) node. Pass null for elseStmt if there's no else.
     *
     * @param cond     the condition to test
     * @param thenStmt statement to run when cond is true
     * @param elseStmt statement to run when cond is false, or null
     * @precondition cond != null, thenStmt != null
     * @postcondition this node stores all three fields
     */
    public If(Condition cond, Statement thenStmt, Statement elseStmt)
    {
        this.cond = cond;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    /**
     * Runs thenStmt if cond is true, elseStmt otherwise (if it exists).
     *
     * @param env the environment for evaluating the condition and running branches
     * @precondition env != null
     */
    @Override
    public void exec(Environment env)
    {
        if (cond.eval(env))
        {
            thenStmt.exec(env);
        }
        else if (elseStmt != null)
        {
            elseStmt.exec(env);
        }
    }

    /**
     * Compiles cond so that a false outcome jumps past the THEN branch. With
     * no ELSE the false target is the endif label; with an ELSE it is the
     * else label, and the THEN branch jumps unconditionally past the ELSE.
     *
     * @param e emitter to write MIPS to
     * @precondition e != null
     * @postcondition either thenStmt's or elseStmt's code runs at runtime
     */
    @Override
    public void compile(Emitter e)
    {
        int id = e.nextLabelID();
        String endLabel = "endif" + id;
        if (elseStmt == null)
        {
            cond.compile(e, endLabel);
            thenStmt.compile(e);
            e.emit(endLabel + ":");
        }
        else
        {
            String elseLabel = "else" + id;
            cond.compile(e, elseLabel);
            thenStmt.compile(e);
            e.emit("j " + endLabel);
            e.emit(elseLabel + ":");
            elseStmt.compile(e);
            e.emit(endLabel + ":");
        }
    }
}
