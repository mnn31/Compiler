package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * WRITELN statement -- evaluates the expression and prints it on its own line.
 *
 * @author Manan Gupta
 * @version 2026-05-02
 */
public class Writeln extends Statement
{
    private final Expression exp;

    /**
     * Creates a WRITELN node for the given expression.
     *
     * @param exp expression to evaluate and print
     * @precondition exp != null
     */
    public Writeln(Expression exp)
    {
        this.exp = exp;
    }

    /**
     * Evaluates exp and prints the result with a newline.
     *
     * @param env environment to evaluate the expression in
     * @precondition env != null
     * @postcondition one integer line written to stdout
     */
    @Override
    public void exec(Environment env)
    {
        System.out.println(exp.eval(env));
    }

    /**
     * Compiles the inner expression, prints it as an integer (syscall 1), then
     * prints a newline (syscall 4 against newline label).
     *
     * @param e emitter to write MIPS to
     * @precondition e != null
     * @postcondition the integer and a trailing newline are printed at runtime
     */
    @Override
    public void compile(Emitter e)
    {
        exp.compile(e);
        e.emit("move $a0 $v0\t# print int in $v0");
        e.emit("li $v0 1");
        e.emit("syscall");
        e.emit("la $a0 newline\t# print newline");
        e.emit("li $v0 4");
        e.emit("syscall");
    }
}
