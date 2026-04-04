package scanner;

import java.io.*;

/**
 * ScannerTester is a test class for the Scanner lexical analyzer.
 * It reads the test files and prints the tokens produced by the scanner.
 * 
 * @author Manan Gupta
 * @version 2026-25-03
 * 
 * Usage:
 * <pre>
 *     java scanner.ScannerTester
 * </pre>
 */
public class ScannerTester
{
    /**
     * Main method that reads the test files scannerTest.txt and scannerTestAdvanced.txt,
     * runs the scanner, and prints the tokens one on each line (ending with "EOF").
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args)
    {
        System.out.println("=== Scanner Tester ===\n");
        
        testFile("ScannerTest.txt");
        System.out.println();
        testFile("scannerTestAdvanced.txt");
        
        System.out.println("\n=== Testing Complete ===");
    }
    
    /**
     * Reads a file and runs the scanner on it. Prints each token on its own line.
     * Uses a while loop that continually calls nextToken() until "EOF" is returned.
     * 
     * @param filename the name of the file to scan
     */
    private static void testFile(String filename)
    {
        System.out.println("File: " + filename);
        System.out.println("----------------------------------------");
        
        try
        {
            File file = new File("scanner/" + filename);
            FileInputStream inStream = new FileInputStream(file);
            Scanner scanner = new Scanner(inStream);
            
            boolean gotEOF = false;
            while (!gotEOF)
            {
                try
                {
                    String token = scanner.nextToken();
                    System.out.println(token);
                    
                    if (token.equals("EOF"))
                    {
                        gotEOF = true;
                    }
                }
                catch (ScanErrorException e)
                {
                    System.out.println("ERROR: " + e.getMessage());
                    break;
                }
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("ERROR: Could not find file " + filename);
        }
        catch (Exception e)
        {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}
