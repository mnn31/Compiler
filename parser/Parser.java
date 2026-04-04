package parser;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import ast.*;
import environment.Environment;
import scanner.ScanErrorException;

/**
 * Recursive-descent parser without backtracking. Builds an abstract syntax tree (AST)
 * for a PL/0-style language: expressions, assignments, WRITELN, BEGIN/END, IF/THEN/ELSE,
 * WHILE/DO, FOR/TO/DO, and READLN. Programs are sequences of statements terminated by a period.
 *
 * @author Manan Gupta
 * @version 2026-25-03
 */
public class Parser
{
    private scanner.Scanner scanner;
    private String curToken;

    /**
     * Constructs a parser that reads tokens from the given scanner.
     *
     * @param scan The scanner that supplies tokens from the source program.
     * @precondition scan is open and will supply tokens for the program
     * @postcondition the current token is the first token from the scanner
     * @throws ScanErrorException If the scanner cannot read the first token.
     */
    public Parser(scanner.Scanner scan) throws ScanErrorException
    {
        scanner = scan;
        curToken = scanner.nextToken();
    }

    /**
     * True if the token is a reserved keyword of the language.
     *
     * @param token the token to test
     * @return true if the token is a reserved word
     * @precondition token is non-null
     * @postcondition returns whether this token string is a reserved keyword
     */
    private boolean isKeyword(String token)
    {
        return token.equals("WRITELN") || token.equals("BEGIN") || token.equals("END")
            || token.equals("mod") || token.equals("IF") || token.equals("THEN")
            || token.equals("ELSE") || token.equals("WHILE") || token.equals("DO")
            || token.equals("FOR") || token.equals("TO") || token.equals("READLN")
            || token.equals("REPEAT") || token.equals("UNTIL") || token.equals("BREAK")
            || token.equals("CONTINUE");
    }

    /**
     * True if token is a variable name (letter start), excluding reserved words.
     *
     * @param token token to test
     * @return true if the token is an identifier
     * @precondition token is non-null
     * @postcondition returns whether the token is a non-keyword identifier
     */
    private boolean isId(String token)
    {
        if (isKeyword(token))
        {
            return false;
        }
        return token.length() > 0 && Character.isLetter(token.charAt(0));
    }

    /**
     * True if the token is a relational operator.
     *
     * @param token the token to test
     * @return true if the token is =, <>, or another comparison operator
     * @precondition token is non-null
     * @postcondition returns whether the token is a relational operator
     */
    private boolean isRelop(String token)
    {
        return token.equals("=") || token.equals("<>") || token.equals("<")
            || token.equals(">") || token.equals("<=") || token.equals(">=");
    }

    /**
     * Consumes the current token if it equals the expected string; otherwise throws.
     *
     * @param token the token value that must match the current token
     * @precondition the current token equals the expected token string
     * @postcondition the matching token is consumed and the current token is the next token
     *                from the scanner
     * @throws IllegalArgumentException if the current token does not match
     * @throws ScanErrorException       if the scanner cannot read the next token
     */
    private void eat(String token) throws IllegalArgumentException, ScanErrorException
    {
        if (curToken.equals(token))
        {
            curToken = scanner.nextToken();
        }
        else
        {
            String msg = "Expected token: " + token + " but got: " + curToken;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Parses a number literal into a Number AST node.
     *
     * @precondition the current token is an integer literal
     * @postcondition that literal token is consumed and the current token advances
     * @return AST number node
     */
    private Expression parseNumber() throws IllegalArgumentException, ScanErrorException
    {
        int value = Integer.parseInt(curToken);
        eat(curToken);
        return new ast.Number(value);
    }

    /**
     * Parses a factor: ( expr ) | - factor | id | num.
     *
     * @precondition the current token begins a factor
     * @postcondition the entire factor is consumed from the token stream
     * @return factor expression AST
     */
    private Expression parseFactor() throws IllegalArgumentException, ScanErrorException
    {
        if (curToken.equals("("))
        {
            eat("(");
            Expression e = parseExpr();
            eat(")");
            return e;
        }
        if (curToken.equals("-"))
        {
            eat("-");
            return new BinOp("-", new ast.Number(0), parseFactor());
        }
        if (isId(curToken))
        {
            String name = curToken;
            eat(name);
            return new Variable(name);
        }
        return parseNumber();
    }

    /**
     * Parses a term: factor ( (*|/|mod) factor )*.
     *
     * @precondition the current token begins a term (a factor)
     * @postcondition the entire term is consumed from the token stream
     * @return term expression AST
     */
    private Expression parseTerm() throws IllegalArgumentException, ScanErrorException
    {
        Expression value = parseFactor();
        while (curToken.equals("*") || curToken.equals("/") || curToken.equals("mod"))
        {
            String op = curToken;
            if (curToken.equals("mod"))
            {
                eat("mod");
            }
            else
            {
                eat(curToken);
            }
            value = new BinOp(op, value, parseFactor());
        }
        return value;
    }

    /**
     * Parses an expression: term ( (+|-) term )*.
     *
     * @precondition the current token begins an expression (a term)
     * @postcondition the entire expression is consumed from the token stream
     * @return expression AST
     */
    public Expression parseExpr() throws IllegalArgumentException, ScanErrorException
    {
        Expression value = parseTerm();
        while (curToken.equals("+") || curToken.equals("-"))
        {
            String op = curToken;
            eat(curToken);
            value = new BinOp(op, value, parseTerm());
        }
        return value;
    }

    /**
     * Parses cond: expr relop expr.
     *
     * @precondition the current token begins the left-hand expression
     * @postcondition the condition (two expressions and a relop) is consumed
     * @return condition AST
     */
    public Condition parseCondition() throws IllegalArgumentException, ScanErrorException
    {
        Expression left = parseExpr();
        if (!isRelop(curToken))
        {
            throw new IllegalArgumentException("Expected relational operator, got: " + curToken);
        }
        String relop = curToken;
        eat(relop);
        Expression right = parseExpr();
        return new Condition(left, relop, right);
    }

    /**
     * Parses a single statement.
     *
     * @precondition the current token starts a valid statement
     * @postcondition one complete statement and its terminating semicolon (if required) are
     *                consumed
     * @return statement AST
     */
    public Statement parseStatement() throws IllegalArgumentException, ScanErrorException
    {
        Statement result;
        if (curToken.equals("WRITELN"))
        {
            eat("WRITELN");
            eat("(");
            Expression exp = parseExpr();
            eat(")");
            eat(";");
            result = new Writeln(exp);
        }
        else if (curToken.equals("READLN"))
        {
            eat("READLN");
            eat("(");
            if (!isId(curToken))
            {
                throw new IllegalArgumentException("READLN expects identifier, got: " + curToken);
            }
            String name = curToken;
            eat(name);
            eat(")");
            eat(";");
            result = new Readln(name);
        }
        else if (curToken.equals("IF"))
        {
            eat("IF");
            Condition c = parseCondition();
            eat("THEN");
            Statement thenStmt = parseStatement();
            if (curToken.equals("ELSE"))
            {
                eat("ELSE");
                Statement elseStmt = parseStatement();
                result = new If(c, thenStmt, elseStmt);
            }
            else
            {
                result = new If(c, thenStmt, null);
            }
        }
        else if (curToken.equals("WHILE"))
        {
            eat("WHILE");
            Condition c = parseCondition();
            eat("DO");
            Statement body = parseStatement();
            result = new While(c, body);
        }
        else if (curToken.equals("FOR"))
        {
            eat("FOR");
            if (!isId(curToken))
            {
                throw new IllegalArgumentException("FOR expects identifier, got: " + curToken);
            }
            String loopVar = curToken;
            eat(loopVar);
            eat(":=");
            Expression start = parseExpr();
            eat("TO");
            Expression end = parseExpr();
            eat("DO");
            Statement body = parseStatement();
            result = new For(loopVar, start, end, body);
        }
        else if (curToken.equals("REPEAT"))
        {
            eat("REPEAT");
            Statement body = parseStatement();
            eat("UNTIL");
            Condition c = parseCondition();
            eat(";");
            result = new RepeatUntil(body, c);
        }
        else if (curToken.equals("BREAK"))
        {
            eat("BREAK");
            eat(";");
            result = new BreakStmt();
        }
        else if (curToken.equals("CONTINUE"))
        {
            eat("CONTINUE");
            eat(";");
            result = new ContinueStmt();
        }
        else if (curToken.equals("BEGIN"))
        {
            eat("BEGIN");
            List<Statement> stmts = new ArrayList<Statement>();
            while (!curToken.equals("END"))
            {
                stmts.add(parseStatement());
            }
            eat("END");
            eat(";");
            result = new Block(stmts);
        }
        else if (isId(curToken))
        {
            String varName = curToken;
            eat(varName);
            eat(":=");
            Expression value = parseExpr();
            eat(";");
            result = new Assignment(varName, value);
        }
        else
        {
            throw new IllegalArgumentException("Expected statement, got: " + curToken);
        }
        return result;
    }

    /**
     * Parses a program: stmts until '.' or EOF, consumes '.' if present.
     *
     * @precondition the current token is the first token of the program, or a period, or EOF
     * @postcondition all top-level statements are parsed; a trailing period is consumed if
     *                present
     * @return block containing all top-level statements
     */
    public Block parseProgram() throws IllegalArgumentException, ScanErrorException
    {
        List<Statement> stmts = new ArrayList<Statement>();
        while (!curToken.equals(".") && !curToken.equals("EOF"))
        {
            stmts.add(parseStatement());
        }
        if (curToken.equals("."))
        {
            eat(".");
        }
        return new Block(stmts);
    }

    /**
     * Parses and executes a program in a fresh environment.
     *
     * @precondition the parser is positioned at the start of a program (first token of the source)
     * @postcondition the program through the period or EOF is parsed and executed
     * @throws ScanErrorException       scan errors
     * @throws IllegalArgumentException parse/runtime errors
     */
    public void runProgram() throws ScanErrorException, IllegalArgumentException
    {
        Block program = parseProgram();
        program.exec(new Environment());
    }

    /**
     * Runs the parser on source files; uses the default test list when args is empty.
     * The default list includes parserTest6 for peer review.
     *
     * @param args zero or more paths to source files; empty means run the built-in default suite
     * @precondition when args is non-empty, each path is a valid file path to test
     * @postcondition each file is parsed and executed or an error message is printed
     */
    public static void main(String[] args)
    {
        String[] files;
        if (args.length > 0)
        {
            files = args;
        }
        else
        {
            files = new String[] {
                "parser/parserTest0.txt",
                "parser/parserTest1.txt",
                "parser/parserTest2.txt",
                "parser/parserTest3.txt",
                "parser/parserTest4.txt",
                "parser/parserTest6.txt",
                "parser/parserTest4.5ForLoopReadln.txt",
                "parser/parserTest6_5.txt"
            };
        }
        for (String path : files)
        {
            System.out.println("--- " + path + " ---");
            try
            {
                FileInputStream in = new FileInputStream(path);
                scanner.Scanner scan = new scanner.Scanner(in);
                Parser p = new Parser(scan);
                p.runProgram();
                System.out.println("Parser completed successfully.");
            }
            catch (java.io.FileNotFoundException e)
            {
                System.err.println("File not found: " + path);
            }
            catch (Exception e)
            {
                String msg = e.getMessage();
                if (msg == null)
                {
                    msg = e.getClass().getName();
                }
                System.err.println("Error: " + msg);
            }
        }
    }
}
