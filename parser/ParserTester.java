package parser;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import ast.Readln;
import scanner.*;

/**
 * Test driver for the AST-based Parser. Each file is parsed into an abstract syntax tree
 * and executed in a fresh Environment. Programs are statement lists terminated by a period
 * or EOF.
 *
 * <p>The default suite builds Statement and Expression trees and runs them with exec and eval.
 * The peer review sheet uses parserTest6 as the main checkoff; it runs after parserTest0
 * through parserTest4. Tests that use READLN get scripted standard input from this class so
 * the full default run works without piping; see prepareStdinForPath.
 *
 * @author Manan Gupta
 * @version 2026-25-03
 */
public class ParserTester
{
    private static final InputStream ORIGINAL_SYSTEM_IN = System.in;

    /**
     * Default test files when no command-line args are given: parserTest0 through parserTest4,
     * then parserTest6 (peer review), then parserTest4.5ForLoopReadln and parserTest6_5.
     */
    private static final String[] DEFAULT_FILES = {
        "parser/parserTest0.txt",
        "parser/parserTest1.txt",
        "parser/parserTest2.txt",
        "parser/parserTest3.txt",
        "parser/parserTest4.txt",
        "parser/parserTest6.txt",
        "parser/parserTest4.5ForLoopReadln.txt",
        "parser/parserTest6_5.txt"
    };

    /**
     * Redirects standard input for known READLN test files so each program gets one integer
     * without manual piping. Other paths leave stdin unchanged.
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
     * Parses a single file: builds a scanner from the file input stream, creates a Parser
     * instance, and parses all statements until EOF. The scanner returns "EOF" when a
     * period is encountered or the stream ends (hasNext() returns false).
     *
     * @param path Path to the source file to parse.
     * @precondition path refers to a readable source file when execution begins
     * @postcondition the file is parsed and run; standard input is restored to the original
     *                stream
     * @throws FileNotFoundException If the file cannot be opened.
     * @throws IOException If reading or closing the file fails.
     * @throws ScanErrorException If the scanner encounters an invalid character.
     * @throws IllegalArgumentException If the token stream is invalid for the grammar.
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
     * Entry point for the parser tester. Runs the parser on one or more source files.
     * If args are provided, each is used as a file path; otherwise the default suite runs
     * (parserTest0–4, parserTest6, then READLN tests). For each file, parses all statements
     * until EOF and prints success or error.
     *
     * @param args optional file paths to parse; if empty, the default suite listed above is used
     * @precondition none
     * @postcondition each requested file has been parsed or a clear error was printed
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
