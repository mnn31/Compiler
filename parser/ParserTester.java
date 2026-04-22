package parser;

import ast.Readln;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import scanner.*;

/**
 * Test driver for the AST-based Parser. Each file is parsed into an abstract syntax tree
 * and executed in a fresh Environment. Programs are statement lists terminated by a period
 * or EOF.
 *
 * <p>The default suite covers all parser test files: parserTest0 through parserTest6_5,
 * plus the mod, repeat/break/continue, and IF/ELSE tests. Tests that use READLN get
 * scripted standard input from prepareStdinForPath so the full default run works without
 * manual piping.
 *
 * <p>Expected outputs per file:
 * <ul>
 *   <li>parserTest0: 3</li>
 *   <li>parserTest1: 4, 9, 1</li>
 *   <li>parserTest2: 14, 10, 20</li>
 *   <li>parserTest3: 1, 2, 3</li>
 *   <li>parserTest4: 15</li>
 *   <li>parserTest5: 10, 10, 7</li>
 *   <li>parserTest6: 15, 5, 3, 0..9</li>
 *   <li>parserTestMod: 1</li>
 *   <li>parserTest4.5ForLoopReadln (input=42): 84, 42..97, 98..84</li>
 *   <li>parserTest6_5 (input=5): 66, 11, 6, 0..9, 1, 3, 6</li>
 *   <li>parserTestRepeatBreakContinue: 1, 1, 2, 1, 3, 6, 10, 15, 21, 28, 36, 45, 55</li>
 *   <li>parserTest7 (procedures + globals): 15, 5, 3, 0..9, 10, 12</li>
 *   <li>parserTest8 (procedure with args): 15, 5, 3, 0..9, 3, 4, 10, 3, 14</li>
 *   <li>parserTest8_5 (env hierarchy): 7, 3, 0</li>
 *   <li>parserTestProcedures (Ex1 simple): 5</li>
 *   <li>parserTestProcedures2 (Ex2 args): 1, 2, 3, 4, 5</li>
 *   <li>parserTestProcedures3 (Ex3 scope): 5, 2</li>
 *   <li>parserTestProcedures4 (Ex4 return): 5, 10</li>
 *   <li>parserTestProcedures5 (scope no clobber): 5, 3</li>
 *   <li>parserTestProcedures6 (factorial): 120, 1, 720</li>
 *   <li>parserTestProcedures7 (loops + procs): 1, 4, 9, 16, 25, 55</li>
 *   <li>parserTestProcedures8 (mutual recursion): 1, 0, 1, 0, 1</li>
 *   <li>parserTestProcedures9 (zero procs regression): 10, 20, 30</li>
 * </ul>
 *
 * @author Manan Gupta
 * @version 2026-25-03
 */
public class ParserTester
{
    private static final InputStream ORIGINAL_SYSTEM_IN = System.in;

    /**
     * All test files run by default when no command-line args are given.
     * Order: basic expression tests, assignment, IF/ELSE, WHILE/FOR, mod,
     * READLN-based tests, and finally the REPEAT/BREAK/CONTINUE test.
     */
    private static final String[] DEFAULT_FILES = {
        "parser/parserTest0.txt",
        "parser/parserTest1.txt",
        "parser/parserTest2.txt",
        "parser/parserTest3.txt",
        "parser/parserTest4.txt",
        "parser/parserTest5.txt",
        "parser/parserTest6.txt",
        "parser/parserTestMod.txt",
        "parser/parserTest4.5ForLoopReadln.txt",
        "parser/parserTest6_5.txt",
        "parser/parserTestRepeatBreakContinue.txt",
        "parser/parserTest7.txt",
        "parser/parserTest8.txt",
        "parser/parserTest8_5.txt",
        "parser/parserTestProcedures.txt",
        "parser/parserTestProcedures2.txt",
        "parser/parserTestProcedures3.txt",
        "parser/parserTestProcedures4.txt",
        "parser/parserTestProcedures5.txt",
        "parser/parserTestProcedures6.txt",
        "parser/parserTestProcedures7.txt",
        "parser/parserTestProcedures8.txt",
        "parser/parserTestProcedures9.txt"
    };

    /**
     * Redirects standard input for known READLN test files so each program gets scripted
     * integers without manual piping. Other paths leave stdin unchanged.
     *
     * @param path path to the source file (may contain directory segments)
     * @precondition path is non-null
     * @postcondition standard input and the Readln scanner match this test file's needs
     */
    private static void prepareStdinForPath(String path)
    {
        if (path.contains("parserTest4.5"))
        {
            System.setIn(new ByteArrayInputStream(
                "42\n".getBytes(StandardCharsets.UTF_8)));
        }
        else if (path.contains("parserTest6_5"))
        {
            System.setIn(new ByteArrayInputStream(
                "5\n".getBytes(StandardCharsets.UTF_8)));
        }
        else
        {
            System.setIn(ORIGINAL_SYSTEM_IN);
        }
        Readln.resetStdinScanner();
    }

    /**
     * Parses and runs a single source file: builds a scanner, creates a Parser, and
     * executes all statements until EOF. Restores stdin afterwards.
     *
     * @param path path to the source file to parse and run
     * @precondition path refers to a readable source file
     * @postcondition the file is parsed and executed; stdin is restored to the original stream
     * @throws FileNotFoundException if the file cannot be opened
     * @throws IOException if reading or closing the file fails
     * @throws ScanErrorException if the scanner encounters an invalid character
     * @throws IllegalArgumentException if the token stream is invalid for the grammar
     */
    private static void parseFile(String path) throws FileNotFoundException, IOException,
            ScanErrorException, IllegalArgumentException
    {
        prepareStdinForPath(path);
        try (FileInputStream in = new FileInputStream(path))
        {
            scanner.Scanner scan = new scanner.Scanner(in);
            Parser parser = new Parser(scan);
            parser.runProgram();
            System.out.println("Parser completed successfully.");
        }
        finally
        {
            System.setIn(ORIGINAL_SYSTEM_IN);
            Readln.resetStdinScanner();
        }
    }

    /**
     * Entry point. Runs the parser on one or more source files.
     * If args are provided each is used as a file path; otherwise the full default suite
     * runs (parserTest0 through parserTestRepeatBreakContinue).
     *
     * @param args optional file paths to parse; if empty the default suite is used
     * @precondition none
     * @postcondition each requested file has been parsed and run, or a clear error was printed
     */
    public static void main(String[] args)
    {
        String[] files = (args.length > 0) ? args : DEFAULT_FILES;

        for (String path : files)
        {
            System.out.println("--- " + path + " ---");
            try
            {
                parseFile(path);
            }
            catch (FileNotFoundException e)
            {
                System.err.println("woops. File not found: " + path);
            }
            catch (IllegalArgumentException e)
            {
                System.err.println("woops. Parse error: " + e.getMessage());
            }
            catch (ScanErrorException e)
            {
                System.err.println("woops. Scan error: " + e.getMessage());
            }
            catch (Exception e)
            {
                String msg = e.getMessage();
                if (msg == null)
                {
                    msg = e.getClass().getName();
                }
                System.err.println("woops. Error: " + msg);
            }
        }
    }
}
