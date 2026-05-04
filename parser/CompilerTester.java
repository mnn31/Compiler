package parser;

import ast.Program;
import java.io.FileInputStream;
import scanner.ScanErrorException;

/**
 * Test driver that parses a Pascal source file and uses the AST's compile
 * method to emit a runnable MIPS .asm file.
 *
 * Usage: java parser.CompilerTester input.txt output.asm
 *
 * With no args, the default suite compiles parserTest9.txt and max.txt.
 *
 * @author Manan Gupta
 * @version 2026-05-02
 */
public class CompilerTester
{
    /**
     * Default input/output pairs run when no command-line args are given.
     * Each pair is {sourcePath, asmOutputPath}.
     */
    private static final String[][] DEFAULT_PAIRS = {
        {"parser/parserTest9.txt", "parser/parserTest9.asm"},
        {"parser/max.txt", "parser/max.asm"},
        {"parser/parserTest9_5.txt", "parser/parserTest9_5.asm"}
    };

    /**
     * Parses sourcePath and writes its compiled MIPS to outputPath.
     *
     * @param sourcePath path to a Pascal source file
     * @param outputPath path of the .asm file to create
     * @precondition sourcePath is readable and outputPath is writable
     * @postcondition outputPath contains a runnable MIPS program for the source
     * @throws Exception if any parse, scan, or I/O error occurs
     */
    private static void compile(String sourcePath, String outputPath) throws Exception
    {
        try (FileInputStream in = new FileInputStream(sourcePath))
        {
            scanner.Scanner scan = new scanner.Scanner(in);
            Parser parser = new Parser(scan);
            Program program = parser.parseProgram();
            program.compile(outputPath);
        }
    }

    /**
     * Entry point. With two args, compiles a single source/output pair;
     * with none, compiles the default suite.
     *
     * @param args optional input.txt output.asm pair
     * @precondition each path is valid for its direction (read/write)
     * @postcondition every requested .asm file has been written or an error logged
     */
    public static void main(String[] args)
    {
        String[][] pairs;
        if (args.length == 2)
        {
            pairs = new String[][] {{args[0], args[1]}};
        }
        else
        {
            pairs = DEFAULT_PAIRS;
        }
        for (String[] pair : pairs)
        {
            String src = pair[0];
            String dst = pair[1];
            System.out.println("--- " + src + " -> " + dst + " ---");
            try
            {
                compile(src, dst);
                System.out.println("compiled successfully!");
            }
            catch (ScanErrorException e)
            {
                System.err.println("woops. Scan error: " + e.getMessage());
            }
            catch (IllegalArgumentException e)
            {
                System.err.println("woops. Parse error: " + e.getMessage());
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
