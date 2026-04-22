package scanner;

import java.io.*;

/**
 * Scanner is a simple lexical scanner for Compilers and Interpreters lab exercise 1.
 * It tokenizes input from either an InputStream or String and provides methods to retrieve
 * individual tokens from the input stream.
 * 
 * @author Manan Gupta
 * @version 2026-25-03
 * 
 * Usage:
 *     FileInputStream inStream = new FileInputStream(new File("program.txt"));
 *     Scanner scanner = new Scanner(inStream);
 *     
 *     // Or scanning from a string:
 *     Scanner scanner = new Scanner("if (x > 5) { y = 10; }");
 *     
 *     // Retrieve and process tokens:
 *     while (scanner.hasNext()) {
 *         String token = scanner.nextToken();
 *         System.out.println("Token: " + token);
 *     }
 * 
 * Lab questions:
 * 1. What token does 'I' then 'F' represent?
 *    We don't know what 'I' and then 'F' mean yet, because we must check if there is NOT a 
 *    letter or digit following it; 
 *    because if there is, then it would be a variable name.
 * 2. What if the next character had been a newline?
 *    If the next character was a newline, we would skip over 
 *    the newline (since its like a whitespace)
 *    because there is no letter or digit following it.
 * 3. What if the next character had been an open parentheses?
 *    If the next character was an open parentheses, we would conclude that 'I' and 
 *    then 'F' form the keyword "if" 
 *    because there is no letter or digit following it.
 * 
 * Hint #4 questions:
 * 1. What will be the parameter you pass to eat?
 *    The parameter passed to eat() will be the currentChar that we want to consume/verify. 
 *    For example, when scanning a number, we pass currentChar to eat() to consume it and advance 
 *    to the next character.
 *    This ensures we're consuming the character we expect to see.
 * 2. Can you think about how this lookahead might be useful later?
 *    Using currentChar to peek at the next character without consuming it is useful for:
 *    - Determining token boundaries (like knowing when an identifier ends because the next char 
 *    isn't a letter/digit)
 *    - Parsing multi-character operators ("==" vs "=", ">=" vs ">")
 *    - Handling kw vs id (same as lab questions)
 */ 
public class Scanner
{
    private BufferedReader in;
    private char currentChar;
    private boolean eof;
    private static final boolean DEBUG = false;
    
    /**
     * Scanner constructor for construction of a scanner that 
     * uses an InputStream object for input.  
     * Usage: 
     *  FileInputStream inStream = new FileInputStream(new File("file name"));
     *  Scanner lex = new Scanner(inStream);
     * 
     * @param inStream the input stream to use
     */
    public Scanner(InputStream inStream)
    {
        in = new BufferedReader(new InputStreamReader(inStream));
        eof = false;
        getNextChar();
    }

    /**
     * Scanner constructor for constructing a scanner that
     * scans a given input string.  It sets the eof flag and then reads
     * the first character of the input string into currentChar.
     * Usage: Scanner lex = new Scanner(input_string);
     * 
     * @param inString the string to scan
     */
    public Scanner(String inString)
    {
        in = new BufferedReader(new StringReader(inString));
        eof = false;
        getNextChar();
    }

    /**
     * Checks for and skips single-line (//) comments.
     * Returns a code indicating whether a comment was found, skipped, or EOF was reached.
     * 
     * @return 0 if no comment started; 1 if comment was skipped; 2 if EOF reached
     * @throws ScanErrorException if mark/reset operations fail
     */
    private int checkAndIgnoreComments() throws ScanErrorException
    {
        if (currentChar == '/')
        {
            try
            {
                in.mark(1);
            }
            catch (IOException yolo)
            {
                throw new ScanErrorException("in.mark() had an error");
            }
            eat(currentChar);
            if (currentChar == '/')
            {
                eat(currentChar);
                // skip until newline
                while (hasNext() && currentChar != '\n' && currentChar != '\r')
                {
                    eat(currentChar);
                }
                if (!hasNext())
                {
                    return 2;
                }
                // Skip the newline character if it is there
                if (hasNext() && (currentChar == '\n' || currentChar == '\r'))
                {
                    eat(currentChar);
                    // Skip \r\n if present
                    if (hasNext() && currentChar == '\n')
                    {
                        eat(currentChar);
                    }
                }
                return 1;
            }
            else
            {
                try
                {
                    in.reset();
                    currentChar = '/';
                }
                catch (IOException yolo1)
                {
                    throw new ScanErrorException("in.reset() had an error");
                }
            }
        }
        return 0;
    }
    /**
     * Advances to the next character in the input stream. Sets eof to true if 
     * end-of-stream reached or a period is encountered.
     *
     * @precondition the BufferedReader in has been initialized
     * @postcondition currentChar is the next character read; eof is true if '.' or end of stream
     */
    private void getNextChar()
    {
        try
        {
            currentChar = (char) in.read();
            // check for ending period
            if (currentChar == '.')
            {
                eof = true;
            }
            else
            {
                eof = (currentChar == (char) -1);
            }
        }
        catch (IOException e)
        {
            eof = true;
            currentChar = (char) -1;
            System.exit(1);
        }
    }
    
    /**
     * Returns whether more characters (and thus tokens) are available. A period (.)
     * signifies end of file; when a period is encountered or the stream ends, this
     * method returns false.
     *
     * @return true if not at EOF; false when a period is encountered or end of stream is reached
     */
    public boolean hasNext()
    {
        return !eof;
    }

    /**
     * Prints a debug message if DEBUG mode is enabled.
     *
     * @param message the debug message to print (ignored when DEBUG is false)
     */
    private void printD(String message)
    {
        if (DEBUG) 
        {
            System.out.println(message);
        }
    }
    
    /**
     * Returns the next token from the input. Skips whitespace and comments,
     * then scans identifiers, numbers, or operators. A period (.) signifies
     * end of file; when a period is encountered or the stream ends, the token
     * "EOF" is returned (and hasNext() returns false).
     *
     * @return the next token as a String, or "EOF" when a period is encountered or at end of file
     * @throws ScanErrorException if an unknown character is encountered
     */
    public String nextToken() throws ScanErrorException
    {
        printD("--------------------------------");
        printD("nextToken() called");
        printD("currentChar: " + currentChar);
        // Check if we're at EOF (either from period or actual EOF)
        if (!hasNext())
        {
            return "EOF";
        }
        eatWhitespaces();
        int commentOrEOF = checkAndIgnoreComments();
        printD("commentOrEOF: " + commentOrEOF);
        printD("currentChar: " + currentChar);
        if (commentOrEOF == 1)
        {
            printD("Comment found, skipping...");
            return nextToken();
        }
        else if (!hasNext() || commentOrEOF == 2)
        {
            return "EOF";
        }
        String result = "";
        if (isOperand(currentChar))
        {
            result = scanOperand();
        }
        else if (isDigit(currentChar))
        {
            result = scanNumber();
        }
        else if (isLetter(currentChar))
        {
            result = scanIdentifier();
        }
        else
        {
            throw new ScanErrorException("unknown character '" + currentChar + "'");
        }
        return result;
    }

    /**
     * Skips all whitespace characters.
     * 
     * @throws ScanErrorException if eat() fails
     */
    private void eatWhitespaces() throws ScanErrorException
    {
        while (isWhiteSpace(currentChar))
        {
            eat(currentChar);
        }
    }

    /**
     * Checks if a character is a digit (0–9).
     * 
     * @param c the character to check
     * @return true if c is '0' through '9'; false otherwise
     */
    public static boolean isDigit(char c)
    {
        return (c >= '0' && c <= '9');
    }

    /**
     * Checks if a character is a letter (a–z or A–Z).
     * 
     * @param c the character to check
     * @return true if c is a letter; false otherwise
     */
    public static boolean isLetter(char c)
    {
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
    }

    /**
     * Checks if a character is whitespace (space, tab, CR, LF).
     * 
     * @param c the character to check
     * @return true if c is whitespace; false otherwise
     */
    public static boolean isWhiteSpace(char c)
    {
        return (c == ' ' || c == '\t' || c == '\r' || c == '\n');
    }
    /**
     * Consumes the current character if it matches the expected value; advances to next character.
     *
     * @param expected the character that must equal currentChar
     * @throws ScanErrorException if expected does not match currentChar
     */
    private void eat(char expected) throws ScanErrorException
    {
        if (expected == currentChar)
        {
            getNextChar();
        }
        else
        {
            throw new ScanErrorException("Illegal character---expected " + expected + 
                " found " + currentChar);
        }
    }
    /**
     * Scans and returns a numeric token (one or more digits).
     *
     * @precondition currentChar is a digit
     * @postcondition currentChar is the first character after the number
     * @return the number as a String
     * @throws ScanErrorException if eat() fails
     */
    private String scanNumber() throws ScanErrorException
    {
        String s = "";
        while (isDigit(currentChar))
        {
            s += currentChar;
            eat(currentChar);
        }
        return s;
    }

    /**
     * Scans and returns an identifier or keyword (letters and digits, must start with letter).
     *
     * @precondition currentChar is a letter
     * @postcondition currentChar is the first character after the identifier
     * @return the identifier as a String
     * @throws ScanErrorException if identifier does not start with a letter
     */
    private String scanIdentifier() throws ScanErrorException
    {
        String s = "";
        while (isLetter(currentChar) || isDigit(currentChar))
        {
            if (s.equals("") && !isLetter(currentChar))
            {
                throw new ScanErrorException("wrong type");
            }
            s += currentChar;
            eat(currentChar);
        }
        return s;
    }

    /**
     * Scans and returns a single- or multi-character operator (= + - * / % ( ) ; <= >= <> :=).
     *
     * @precondition currentChar is an operator character
     * @postcondition currentChar is the first character after the operator
     * @return the operator as a String
     * @throws ScanErrorException if currentChar is not a valid operator
     */
    private String scanOperand() throws ScanErrorException
    {
        String s = "";
        if (isOperand(currentChar))
        {
            s += currentChar;
            char firstChar = currentChar;
            eat(currentChar);
            
            // Check for multi-character operators using lookahead
            if (firstChar == '<' && currentChar == '=')
            {
                // <= operator
                s += currentChar;
                eat(currentChar);
            }
            else if (firstChar == '<' && currentChar == '>')
            {
                // <> operator
                s += currentChar;
                eat(currentChar);
            }
            else if (firstChar == '>' && currentChar == '=')
            {
                // >= operator
                s += currentChar;
                eat(currentChar);
            }
            else if (firstChar == ':' && currentChar == '=')
            {
                // := operator
                s += currentChar;
                eat(currentChar);
            }
        }
        else
        {
            throw new ScanErrorException("wrong type");
        }
        return s;
    }

    /**
     * Checks if a character is a valid operator start.
     * 
     * @param c the character to check
     * @return true if c is an operator; false otherwise
     */
    private boolean isOperand(char c)
    {
        return (c == '=' || c == '+' || c == '-' ||
            c == '*' || c == '/' || c == '%' ||
            c == '(' || c == ')' || c == '<' ||
            c == '>' || c == ':' || c == ';' || c == ',');
    }
}
