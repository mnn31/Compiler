package parser;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import ast.*;
import environment.Environment;
import scanner.ScanErrorException;

/**
 * Recursive-descent parser that builds an AST instead of evaluating directly.
 * Handles expressions, assignments, WRITELN/READLN, BEGIN/END blocks, IF/ELSE,
 * WHILE, FOR, REPEAT/UNTIL, BREAK, and CONTINUE.
 *
 * <p>Each parseXXX method consumes tokens and returns the corresponding AST node.
 * Actual execution happens later when exec/eval is called on the tree.
 *
 * @author Manan Gupta
 * @version 2026-25-03
 */
public class Parser
{
    private scanner.Scanner scanner;
    private String curToken;

    /**
     * Creates a parser reading from the given scanner.
     * Immediately loads the first token into curToken.
     *
     * @param scan the scanner to read tokens from
     * @precondition scan != null and is ready to produce tokens
     * @postcondition curToken holds the first token of the input
     * @throws ScanErrorException if the very first nextToken() call fails
     */
    public Parser(scanner.Scanner scan) throws ScanErrorException
    {
        scanner = scan;
        curToken = scanner.nextToken();
    }

    /**
     * Returns true if token is a reserved keyword (so we don't treat it as an identifier).
     *
     * @param token the token string to check
     * @return true if token is a keyword, false otherwise
     * @precondition token != null
     * @postcondition token is unchanged; no side effects
     */
    private boolean isKeyword(String token)
    {
        return token.equals("WRITELN") || token.equals("BEGIN") || token.equals("END")
            || token.equals("mod") || token.equals("IF") || token.equals("THEN")
            || token.equals("ELSE") || token.equals("WHILE") || token.equals("DO")
            || token.equals("FOR") || token.equals("TO") || token.equals("READLN")
            || token.equals("REPEAT") || token.equals("UNTIL") || token.equals("BREAK")
            || token.equals("CONTINUE") || token.equals("PROCEDURE");
    }

    /**
     * Returns true if token looks like a user-defined identifier
     * (starts with a letter and isn't a keyword).
     *
     * @param token token to check
     * @return true if it's a valid non-keyword identifier
     * @precondition token != null
     * @postcondition no side effects
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
     * Checks if token is one of the six relational operators.
     *
     * @param token token to check
     * @return true if token is =, &lt;&gt;, &lt;, &gt;, &lt;=, or &gt;=
     * @precondition token != null
     * @postcondition no side effects
     */
    private boolean isRelop(String token)
    {
        return token.equals("=") || token.equals("<>") || token.equals("<")
            || token.equals(">") || token.equals("<=") || token.equals(">=");
    }

    /**
     * Advances past curToken if it matches token, otherwise throws.
     * This is the standard "eat" pattern from class.
     *
     * @param token the expected current token
     * @precondition token != null
     * @postcondition curToken is now the token that follows the consumed one
     * @throws IllegalArgumentException if curToken doesn't match token
     * @throws ScanErrorException if the scanner blows up reading the next token
     */
    private void eat(String token) throws IllegalArgumentException, ScanErrorException
    {
        if (curToken.equals(token))
        {
            curToken = scanner.nextToken();
        }
        else
        {
            throw new IllegalArgumentException(
                "expected '" + token + "' but got '" + curToken + "'");
        }
    }

    /**
     * Parses an integer literal and wraps it in a Number node.
     *
     * @precondition curToken is a valid integer string
     * @postcondition curToken advances past the number
     * @return a Number node holding the parsed int
     * @throws IllegalArgumentException if curToken isn't a valid integer
     * @throws ScanErrorException if the scanner fails advancing
     */
    private Expression parseNumber() throws IllegalArgumentException, ScanErrorException
    {
        int value = Integer.parseInt(curToken);
        eat(curToken);
        return new ast.Number(value);
    }

    /**
     * Parses a factor -- one of: (expr), -factor, id(args) procedure call,
     * variable name, or number.
     *
     * @precondition curToken starts a valid factor
     * @postcondition all tokens making up the factor are consumed
     * @return the Expression node for the factor
     * @throws IllegalArgumentException if curToken doesn't start a valid factor
     * @throws ScanErrorException if the scanner fails
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
            // id '(' maybeargs ')' is a procedure call, but otherwise just a variable lol
            if (curToken.equals("("))
            {
                eat("(");
                List<Expression> args = new ArrayList<Expression>();
                if (!curToken.equals(")"))
                {
                    args.add(parseExpr());
                }
                while (!curToken.equals(")"))
                {
                    eat(",");
                    args.add(parseExpr());
                }
                eat(")");
                return new ProcedureCall(name, args);
            }
            return new Variable(name);
        }
        return parseNumber();
    }

    /**
     * Parses a term (handles *, /, mod with left-associativity).
     * Grammar: term -&gt; factor ( (*|/|mod) factor )*
     *
     * @precondition curToken starts a factor
     * @postcondition all tokens in the term are consumed
     * @return the Expression node for the term
     * @throws IllegalArgumentException if something unexpected shows up
     * @throws ScanErrorException if the scanner fails
     */
    private Expression parseTerm() throws IllegalArgumentException, ScanErrorException
    {
        Expression value = parseFactor();
        while (curToken.equals("*") || curToken.equals("/") || curToken.equals("mod"))
        {
            String op = curToken;
            eat(op);
            value = new BinOp(op, value, parseFactor());
        }
        return value;
    }

    /**
     * Parses an expression (handles + and - with left-associativity).
     * Grammar: expr -&gt; term ( (+|-) term )*
     *
     * @precondition curToken starts a term
     * @postcondition all tokens in the expression are consumed
     * @return the Expression node
     * @throws IllegalArgumentException if something unexpected shows up
     * @throws ScanErrorException if the scanner fails
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
     * Parses a condition (two expressions separated by a relational operator).
     *
     * @precondition curToken starts an expression
     * @postcondition left expr, relop, and right expr are all consumed
     * @return a Condition node
     * @throws IllegalArgumentException if no relop is found between the two expressions
     * @throws ScanErrorException if the scanner fails
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
     * Parses one statement and returns the corresponding AST node.
     * Handles WRITELN, READLN, IF, WHILE, FOR, REPEAT, BREAK, CONTINUE,
     * BEGIN/END blocks, and variable assignment.
     *
     * @precondition curToken is the first token of a valid statement
     * @postcondition the full statement (including any trailing semicolon) is consumed
     * @return the Statement node
     * @throws IllegalArgumentException if curToken doesn't start any known statement
     * @throws ScanErrorException if the scanner fails mid-parse
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
            throw new IllegalArgumentException("unexpected token: '" + curToken + "'");
        }
        return result;
    }

    /**
     * Parses a single PROCEDURE declaration:
     * PROCEDURE id ( maybeparms ) ; stmt
     *
     * @precondition curToken == "PROCEDURE"
     * @postcondition the declaration (including its body) is fully consumed
     * @return the ProcedureDeclaration node
     * @throws IllegalArgumentException if the declaration is malformed
     * @throws ScanErrorException if the scanner fails
     */
    private ProcedureDeclaration parseProcedureDeclaration()
        throws IllegalArgumentException, ScanErrorException
    {
        eat("PROCEDURE");
        if (!isId(curToken))
        {
            throw new IllegalArgumentException(
                "PROCEDURE expects identifier, got: " + curToken);
        }
        String name = curToken;
        eat(name);
        eat("(");
        List<String> params = new ArrayList<String>();
        if (!curToken.equals(")"))
        {
            if (!isId(curToken))
            {
                throw new IllegalArgumentException(
                    "parameter name expected, got: " + curToken);
            }
            params.add(curToken);
            eat(curToken);
            while (curToken.equals(","))
            {
                eat(",");
                if (!isId(curToken))
                {
                    throw new IllegalArgumentException(
                        "parameter name expected, got: " + curToken);
                }
                params.add(curToken);
                eat(curToken);
            }
        }
        eat(")");
        eat(";");
        Statement body = parseStatement();
        return new ProcedureDeclaration(name, params, body);
    }

    /**
     * Parses the whole program. Consumes any leading PROCEDURE declarations,
     * then a single main statement, then the terminating period (if present).
     * Grammar: program -&gt; PROCEDURE id ( maybeparms ) ; stmt program | stmt .
     *
     * @precondition curToken is the very first token of the source
     * @postcondition all declarations and the main statement are consumed;
     *                trailing '.' is consumed if present
     * @return a Program node wrapping the declarations and the main statement
     * @throws IllegalArgumentException if the source doesn't match the grammar
     * @throws ScanErrorException if the scanner fails
     */
    public Program parseProgram() throws IllegalArgumentException, ScanErrorException
    {
        List<ProcedureDeclaration> procs = new ArrayList<ProcedureDeclaration>();
        while (curToken.equals("PROCEDURE"))
        {
            procs.add(parseProcedureDeclaration());
        }
        Statement main;
        if (!curToken.equals("EOF"))
        {
            main = parseStatement();
        }
        else
        {
            // No main statement -- use an empty block so exec() is a no-op.
            main = new Block(new ArrayList<Statement>());
        }
        // The scanner already consumed the terminating '.' by setting EOF.
        return new Program(procs, main);
    }

    /**
     * Convenience method: parses the program then immediately runs it in a new environment.
     *
     * @precondition the parser is at the start of a valid program
     * @postcondition the program has been fully parsed and executed
     * @throws ScanErrorException if the scanner runs into trouble
     * @throws IllegalArgumentException if there's a parse or runtime error
     */
    public void runProgram() throws ScanErrorException, IllegalArgumentException
    {
        Program program = parseProgram();
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
                "parser/parserTest5.txt",
                "parser/parserTest6.txt",
                "parser/parserTestMod.txt",
                "parser/parserTest4.5ForLoopReadln.txt",
                "parser/parserTest6_5.txt",
                "parser/parserTestRepeatBreakContinue.txt",
                "parser/parserTest7.txt",
                "parser/parserTest8.txt",
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
