package ast;

import java.util.Scanner;
import emitter.Emitter;
import environment.Environment;

/**
 * READLN statement -- reads one integer from stdin and stores it in var.
 * The static scanner is shared across all READLN nodes so we don't open
 * multiple scanners on System.in (that caused bugs during testing).
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class Readln extends Statement
{
    private final String var;
    private static Scanner stdinScanner;

    /**
     * Resets the shared stdin scanner. Call this when System.in has been
     * redirected (e.g. in ParserTester when injecting scripted input).
     *
     * @postcondition stdinScanner now reads from the current System.in
     */
    public static void resetStdinScanner()
    {
        stdinScanner = new Scanner(System.in);
    }

    /**
     * Creates a READLN node that reads into the given variable.
     *
     * @param var name of the variable to store the input in
     * @precondition var != null
     */
    public Readln(String var)
    {
        this.var = var;
    }

    /**
     * Reads the next integer from stdin and assigns it to var in env.
     * Lazily initializes the scanner if it hasn't been set up yet.
     *
     * @param env environment to store the result in
     * @precondition stdin has an integer available to read
     * @postcondition env.getVariable(var) returns the integer that was read
     */
    @Override
    public void exec(Environment env)
    {
        if (stdinScanner == null)
        {
            stdinScanner = new Scanner(System.in);
        }
        env.setVariable(var, stdinScanner.nextInt());
    }

    /**
     * Compiles to MIPS syscall 5 (read int) and stores the resulting $v0 into
     * var's slot in the .data section.
     *
     * @param e emitter to write MIPS to
     * @precondition e != null; var was emitted in the .data section
     * @postcondition var holds the integer typed by the user at runtime
     */
    @Override
    public void compile(Emitter e)
    {
        e.emit("li $v0 5\t# READLN -- read int syscall");
        e.emit("syscall");
        e.emit("la $t0 var" + var);
        e.emit("sw $v0 ($t0)\t# store input into var" + var);
    }
}
