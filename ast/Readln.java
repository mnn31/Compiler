package ast;

import java.util.Scanner;
import environment.Environment;

/**
 * Statement that reads one integer from standard input into a variable.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class Readln extends Statement
{
    private final String var;
    private static Scanner stdinScanner;

    /**
     * Rebinds standard input for READLN. Call after standard input is redirected, for example
     * from the parser test driver when supplying scripted input for tests.
     *
     * @precondition none
     * @postcondition the static scanner reads from the current standard input stream
     */
    public static void resetStdinScanner()
    {
        stdinScanner = new Scanner(System.in);
    }

    /**
     * Constructs a READLN for the named variable.
     *
     * @param var the variable that receives the next integer from stdin
     * @precondition var is a non-null variable name
     * @postcondition this statement targets that name for the next read
     */
    public Readln(String var)
    {
        this.var = var;
    }

    /**
     * Reads one integer from standard input and stores it under this statement variable name.
     *
     * @param env the runtime environment where the read value is stored
     * @precondition the bound stdin scanner has a next token that parses as an integer
     * @postcondition the target variable is set in env to the integer read from stdin
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
