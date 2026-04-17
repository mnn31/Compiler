package ast;

import java.util.Scanner;
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
}
