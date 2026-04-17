package ll1parser;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ast.*;
import environment.Environment;
import scanner.ScanErrorException;

/**
 * Table-driven LL(1) predictive parser that replaces the recursive descent
 * parser. Uses an explicit parse table built from FIRST and FOLLOW sets
 * to decide which production to apply for each non-terminal, following
 * the LL(1) parsing algorithm from the Parsing Day 3 lecture slides.
 *
 * <p>The core idea (from slides): given a non-terminal on top of the
 * parsing stack and the current lookahead token, consult the 2-D parse
 * table {@code TABLE[nonTerminal][token]} to obtain the production to
 * expand. Terminals on the stack are matched (eaten) against the input.
 *
 * <p>LL(1) Grammar (left-recursion removed via the A-&gt;betaA',
 * A'-&gt;alphaA'|epsilon transformation from the slides):
 * <pre>
 *   program   -&gt; stmtList
 *   stmtList  -&gt; stmt stmtList | epsilon
 *   stmt      -&gt; WRITELN ( expr ) ;
 *              | READLN ( ID ) ;
 *              | IF cond THEN stmt elsePart
 *              | WHILE cond DO stmt
 *              | FOR ID := expr TO expr DO stmt
 *              | REPEAT stmt UNTIL cond ;
 *              | BREAK ;
 *              | CONTINUE ;
 *              | BEGIN stmtList END ;
 *              | ID := expr ;
 *   elsePart  -&gt; ELSE stmt | epsilon
 *   cond      -&gt; expr relop expr
 *   expr      -&gt; term exprTail
 *   exprTail  -&gt; + term exprTail
 *              | - term exprTail
 *              | epsilon
 *   term      -&gt; factor termTail
 *   termTail  -&gt; * factor termTail
 *              | / factor termTail
 *              | mod factor termTail
 *              | epsilon
 *   factor    -&gt; ( expr ) | - factor | ID | NUMBER
 * </pre>
 *
 * <p>FIRST sets (hand-computed following slides rules):
 * <pre>
 *   FIRST(factor)   = { (, -, ID, NUMBER }
 *   FIRST(termTail) = { *, /, mod, epsilon }
 *   FIRST(term)     = FIRST(factor) = { (, -, ID, NUMBER }
 *   FIRST(exprTail) = { +, -, epsilon }
 *   FIRST(expr)     = FIRST(term) = { (, -, ID, NUMBER }
 *   FIRST(cond)     = FIRST(expr) = { (, -, ID, NUMBER }
 *   FIRST(elsePart) = { ELSE, epsilon }
 *   FIRST(stmt)     = { WRITELN, READLN, IF, WHILE, FOR, REPEAT,
 *                        BREAK, CONTINUE, BEGIN, ID }
 *   FIRST(stmtList) = FIRST(stmt) union { epsilon }
 * </pre>
 *
 * <p>FOLLOW sets (hand-computed following slides rules):
 * <pre>
 *   FOLLOW(program)  = { $ }
 *   FOLLOW(stmtList) = { ., EOF, END }
 *   FOLLOW(stmt)     = FOLLOW(stmtList) union FIRST(stmtList)\epsilon
 *                       union { ELSE, UNTIL }
 *   FOLLOW(elsePart) = FOLLOW(stmt)
 *   FOLLOW(expr)     = { ), ;, TO, THEN, DO, =, &lt;&gt;, &lt;, &gt;, &lt;=, &gt;= }
 *   FOLLOW(exprTail) = FOLLOW(expr)
 *   FOLLOW(term)     = { +, - } union FOLLOW(expr)
 *   FOLLOW(termTail) = FOLLOW(term)
 *   FOLLOW(factor)   = { *, /, mod } union FOLLOW(term)
 * </pre>
 *
 * <p>AI Trial (Vibe Coding) chat history: This LL(1) parser was created
 * with Claude (Anthropic Cowork, claude-opus-4-6) on 2026-04-09. The
 * conversation involved: (1) exploring the existing recursive-descent
 * parser, scanner, AST, and environment packages; (2) reading the
 * Parsing Day 3 slides on CFG types, left-recursion removal, FIRST
 * and FOLLOW set computation, and the LL(1) table-driven algorithm;
 * (3) deriving the LL(1) grammar by applying the A-&gt;betaA',
 * A'-&gt;alphaA'|epsilon transformation to the left-recursive expr
 * and term rules; (4) hand-computing FIRST and FOLLOW sets;
 * (5) constructing the predictive parse table; and (6) implementing
 * this table-driven parser that produces the identical AST nodes as
 * the original recursive descent parser.
 *
 * @author Manan Gupta (with Claude AI assistance for ATCS AI Trial)
 * @version 2026-04-09
 */
public class LL1Parser
{
    /**
     * Production number for stmtList -&gt; stmt stmtList.
     */
    private static final int PROD_STMTLIST_STMT = 1;

    /**
     * Production number for stmtList -&gt; epsilon.
     */
    private static final int PROD_STMTLIST_EPS = 2;

    /**
     * Production number for stmt -&gt; WRITELN ( expr ) ;.
     */
    private static final int PROD_STMT_WRITELN = 3;

    /**
     * Production number for stmt -&gt; READLN ( ID ) ;.
     */
    private static final int PROD_STMT_READLN = 4;

    /**
     * Production number for stmt -&gt; IF cond THEN stmt elsePart.
     */
    private static final int PROD_STMT_IF = 5;

    /**
     * Production number for stmt -&gt; WHILE cond DO stmt.
     */
    private static final int PROD_STMT_WHILE = 6;

    /**
     * Production number for stmt -&gt; FOR ID := expr TO expr DO stmt.
     */
    private static final int PROD_STMT_FOR = 7;

    /**
     * Production number for stmt -&gt; REPEAT stmt UNTIL cond ;.
     */
    private static final int PROD_STMT_REPEAT = 8;

    /**
     * Production number for stmt -&gt; BREAK ;.
     */
    private static final int PROD_STMT_BREAK = 9;

    /**
     * Production number for stmt -&gt; CONTINUE ;.
     */
    private static final int PROD_STMT_CONTINUE = 10;

    /**
     * Production number for stmt -&gt; BEGIN stmtList END ;.
     */
    private static final int PROD_STMT_BEGIN = 11;

    /**
     * Production number for stmt -&gt; ID := expr ;.
     */
    private static final int PROD_STMT_ASSIGN = 12;

    /**
     * Production number for elsePart -&gt; ELSE stmt.
     */
    private static final int PROD_ELSE_STMT = 13;

    /**
     * Production number for elsePart -&gt; epsilon.
     */
    private static final int PROD_ELSE_EPS = 14;

    /**
     * Production number for expr -&gt; term exprTail.
     */
    private static final int PROD_EXPR = 15;

    /**
     * Production number for exprTail -&gt; + term exprTail.
     */
    private static final int PROD_EXPRTAIL_PLUS = 16;

    /**
     * Production number for exprTail -&gt; - term exprTail.
     */
    private static final int PROD_EXPRTAIL_MINUS = 17;

    /**
     * Production number for exprTail -&gt; epsilon.
     */
    private static final int PROD_EXPRTAIL_EPS = 18;

    /**
     * Production number for term -&gt; factor termTail.
     */
    private static final int PROD_TERM = 19;

    /**
     * Production number for termTail -&gt; * factor termTail.
     */
    private static final int PROD_TERMTAIL_STAR = 20;

    /**
     * Production number for termTail -&gt; / factor termTail.
     */
    private static final int PROD_TERMTAIL_SLASH = 21;

    /**
     * Production number for termTail -&gt; mod factor termTail.
     */
    private static final int PROD_TERMTAIL_MOD = 22;

    /**
     * Production number for termTail -&gt; epsilon.
     */
    private static final int PROD_TERMTAIL_EPS = 23;

    /**
     * Production number for factor -&gt; ( expr ).
     */
    private static final int PROD_FACTOR_PAREN = 24;

    /**
     * Production number for factor -&gt; - factor.
     */
    private static final int PROD_FACTOR_NEG = 25;

    /**
     * Production number for factor -&gt; ID.
     */
    private static final int PROD_FACTOR_ID = 26;

    /**
     * Production number for factor -&gt; NUMBER.
     */
    private static final int PROD_FACTOR_NUM = 27;

    /**
     * The LL(1) predictive parse table. Maps (nonTerminal, tokenCategory)
     * to a production number. Built once in the static initializer from
     * the hand-computed FIRST and FOLLOW sets, exactly as demonstrated
     * in the Parsing Day 3 slides (the red/white grid tables).
     */
    private static final Map<String, Map<String, Integer>> TABLE =
        new HashMap<String, Map<String, Integer>>();

    static
    {
        buildTable();
    }

    /**
     * The lexical scanner providing tokens.
     */
    private scanner.Scanner scanner;

    /**
     * The current lookahead token (one token of lookahead for LL(1)).
     */
    private String curToken;

    /**
     * Constructs an LL1Parser that reads tokens from the given scanner.
     * Immediately loads the first lookahead token into curToken.
     *
     * @param scan the scanner to read tokens from
     * @precondition scan is not null and ready to produce tokens
     * @postcondition curToken holds the first token of the input
     * @throws ScanErrorException if the first nextToken() call fails
     */
    public LL1Parser(scanner.Scanner scan) throws ScanErrorException
    {
        scanner = scan;
        curToken = scanner.nextToken();
    }

    /**
     * Populates the static LL(1) parse table from the hand-computed
     * FIRST and FOLLOW sets. Each row is a non-terminal; each column
     * is a token category. The cell value is the production number
     * to apply.
     *
     * <p>Table construction rule (from slides):
     * For production A -&gt; alpha, place A-&gt;alpha in TABLE[A][t]
     * for every terminal t in FIRST(alpha). If epsilon is in
     * FIRST(alpha), also place A-&gt;alpha in TABLE[A][t] for every
     * terminal t in FOLLOW(A).
     *
     * @postcondition TABLE is fully populated with all non-terminals
     */
    private static void buildTable()
    {
        // ---- stmtList row ----
        // FIRST(stmt) -> stmtList -> stmt stmtList
        // FOLLOW(stmtList) = {., EOF, END} -> stmtList -> epsilon
        Map<String, Integer> stmtListRow =
            new HashMap<String, Integer>();
        String[] stmtFirstTokens = {"WRITELN", "READLN", "IF", "WHILE",
            "FOR", "REPEAT", "BREAK", "CONTINUE", "BEGIN", "ID"};
        for (String t : stmtFirstTokens)
        {
            stmtListRow.put(t, PROD_STMTLIST_STMT);
        }
        stmtListRow.put(".", PROD_STMTLIST_EPS);
        stmtListRow.put("EOF", PROD_STMTLIST_EPS);
        stmtListRow.put("END", PROD_STMTLIST_EPS);
        TABLE.put("stmtList", stmtListRow);

        // ---- stmt row ----
        // Each keyword/ID uniquely determines the stmt production.
        Map<String, Integer> stmtRow = new HashMap<String, Integer>();
        stmtRow.put("WRITELN", PROD_STMT_WRITELN);
        stmtRow.put("READLN", PROD_STMT_READLN);
        stmtRow.put("IF", PROD_STMT_IF);
        stmtRow.put("WHILE", PROD_STMT_WHILE);
        stmtRow.put("FOR", PROD_STMT_FOR);
        stmtRow.put("REPEAT", PROD_STMT_REPEAT);
        stmtRow.put("BREAK", PROD_STMT_BREAK);
        stmtRow.put("CONTINUE", PROD_STMT_CONTINUE);
        stmtRow.put("BEGIN", PROD_STMT_BEGIN);
        stmtRow.put("ID", PROD_STMT_ASSIGN);
        TABLE.put("stmt", stmtRow);

        // ---- elsePart row ----
        // FIRST(elsePart) for ELSE production: {ELSE}
        // FOLLOW(elsePart) = FOLLOW(stmt) for epsilon production
        Map<String, Integer> elseRow = new HashMap<String, Integer>();
        elseRow.put("ELSE", PROD_ELSE_STMT);
        String[] elseEpsTokens = {".", "EOF", "END", "WRITELN", "READLN",
            "IF", "WHILE", "FOR", "REPEAT", "BREAK", "CONTINUE",
            "BEGIN", "ID", "UNTIL"};
        for (String t : elseEpsTokens)
        {
            elseRow.put(t, PROD_ELSE_EPS);
        }
        TABLE.put("elsePart", elseRow);

        // ---- expr row ----
        // FIRST(expr) = FIRST(term) = FIRST(factor) = {(, -, ID, NUMBER}
        Map<String, Integer> exprRow = new HashMap<String, Integer>();
        String[] exprFirstTokens = {"(", "-", "ID", "NUMBER"};
        for (String t : exprFirstTokens)
        {
            exprRow.put(t, PROD_EXPR);
        }
        TABLE.put("expr", exprRow);

        // ---- exprTail row ----
        // FIRST(exprTail): + -> PLUS, - -> MINUS
        // FOLLOW(expr) = {), ;, =, <>, <, >, <=, >=, TO} -> epsilon
        Map<String, Integer> exprTailRow =
            new HashMap<String, Integer>();
        exprTailRow.put("+", PROD_EXPRTAIL_PLUS);
        exprTailRow.put("-", PROD_EXPRTAIL_MINUS);
        String[] exprFollowTokens = {")", ";", "=", "<>", "<", ">",
            "<=", ">=", "TO", "THEN", "DO"};
        for (String t : exprFollowTokens)
        {
            exprTailRow.put(t, PROD_EXPRTAIL_EPS);
        }
        TABLE.put("exprTail", exprTailRow);

        // ---- term row ----
        // FIRST(term) = FIRST(factor) = {(, -, ID, NUMBER}
        Map<String, Integer> termRow = new HashMap<String, Integer>();
        for (String t : exprFirstTokens)
        {
            termRow.put(t, PROD_TERM);
        }
        TABLE.put("term", termRow);

        // ---- termTail row ----
        // FIRST(termTail): * -> STAR, / -> SLASH, mod -> MOD
        // FOLLOW(term) = {+, -} union FOLLOW(expr) -> epsilon
        Map<String, Integer> termTailRow =
            new HashMap<String, Integer>();
        termTailRow.put("*", PROD_TERMTAIL_STAR);
        termTailRow.put("/", PROD_TERMTAIL_SLASH);
        termTailRow.put("mod", PROD_TERMTAIL_MOD);
        String[] termFollowTokens = {"+", "-", ")", ";", "=", "<>",
            "<", ">", "<=", ">=", "TO", "THEN", "DO"};
        for (String t : termFollowTokens)
        {
            termTailRow.put(t, PROD_TERMTAIL_EPS);
        }
        TABLE.put("termTail", termTailRow);

        // ---- factor row ----
        // Each FIRST(factor) token uniquely determines the production.
        Map<String, Integer> factorRow = new HashMap<String, Integer>();
        factorRow.put("(", PROD_FACTOR_PAREN);
        factorRow.put("-", PROD_FACTOR_NEG);
        factorRow.put("ID", PROD_FACTOR_ID);
        factorRow.put("NUMBER", PROD_FACTOR_NUM);
        TABLE.put("factor", factorRow);
    }

    // ------------------------------------------------------------------
    // Helper methods
    // ------------------------------------------------------------------

    /**
     * Returns true if the token is a reserved keyword of the language.
     *
     * @param token the token string to check
     * @return true if token is one of the language keywords
     * @precondition token is not null
     * @postcondition no side effects
     */
    private boolean isKeyword(String token)
    {
        return token.equals("WRITELN") || token.equals("BEGIN")
            || token.equals("END") || token.equals("mod")
            || token.equals("IF") || token.equals("THEN")
            || token.equals("ELSE") || token.equals("WHILE")
            || token.equals("DO") || token.equals("FOR")
            || token.equals("TO") || token.equals("READLN")
            || token.equals("REPEAT") || token.equals("UNTIL")
            || token.equals("BREAK") || token.equals("CONTINUE");
    }

    /**
     * Returns true if the token is an operator or punctuation symbol.
     *
     * @param token the token string to check
     * @return true if token is an operator or punctuation mark
     * @precondition token is not null
     * @postcondition no side effects
     */
    private boolean isOperatorOrPunctuation(String token)
    {
        return token.equals("=") || token.equals("+") || token.equals("-")
            || token.equals("*") || token.equals("/") || token.equals("(")
            || token.equals(")") || token.equals(";") || token.equals(":=")
            || token.equals("<") || token.equals(">") || token.equals("<=")
            || token.equals(">=") || token.equals("<>");
    }

    /**
     * Returns true if the token is a user-defined identifier (starts
     * with a letter and is not a keyword).
     *
     * @param token the token to check
     * @return true if it is a valid non-keyword identifier
     * @precondition token is not null
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
     * Returns true if the token is one of the six relational operators.
     *
     * @param token the token to check
     * @return true if token is =, &lt;&gt;, &lt;, &gt;, &lt;=, or &gt;=
     * @precondition token is not null
     * @postcondition no side effects
     */
    private boolean isRelop(String token)
    {
        return token.equals("=") || token.equals("<>")
            || token.equals("<") || token.equals(">")
            || token.equals("<=") || token.equals(">=");
    }

    /**
     * Maps a raw token string to its category for parse-table lookup.
     * Keywords and operators map to themselves. Identifiers become "ID".
     * Numeric literals become "NUMBER".
     *
     * @param token the raw token from the scanner
     * @return the category string used as a column key in the table
     * @precondition token is not null
     * @postcondition returns a category recognized by the parse table
     */
    private String tokenCategory(String token)
    {
        if (isKeyword(token) || isOperatorOrPunctuation(token)
            || token.equals("EOF") || token.equals("."))
        {
            return token;
        }
        if (token.length() > 0 && Character.isDigit(token.charAt(0)))
        {
            return "NUMBER";
        }
        if (token.length() > 0 && Character.isLetter(token.charAt(0)))
        {
            return "ID";
        }
        return token;
    }

    /**
     * Looks up the parse table for the given non-terminal and the
     * current lookahead token. Returns the production number to apply.
     *
     * @param nonTerminal the non-terminal to expand
     * @return the production number from TABLE[nonTerminal][curToken]
     * @precondition nonTerminal is a valid key in TABLE
     * @postcondition no tokens consumed; table is unchanged
     * @throws IllegalArgumentException if no entry exists (syntax error)
     */
    private int lookupTable(String nonTerminal)
    {
        String category = tokenCategory(curToken);
        Map<String, Integer> row = TABLE.get(nonTerminal);
        if (row == null || !row.containsKey(category))
        {
            throw new IllegalArgumentException(
                "LL(1) parse error: no entry for ["
                + nonTerminal + ", " + category
                + "] (curToken='" + curToken + "')");
        }
        return row.get(category);
    }

    /**
     * Consumes the current token if it matches the expected string,
     * then advances to the next token. This is the standard "eat"
     * (match-and-advance) operation for predictive parsers.
     *
     * @param token the expected current token
     * @precondition token is not null
     * @postcondition curToken is the token following the consumed one
     * @throws IllegalArgumentException if curToken does not match token
     * @throws ScanErrorException if the scanner fails on nextToken
     */
    private void eat(String token) throws ScanErrorException
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

    // ------------------------------------------------------------------
    // Non-terminal parsing methods (each consults the parse table)
    // ------------------------------------------------------------------

    /**
     * Parses the program non-terminal: program -&gt; stmtList.
     * Consumes an optional trailing period.
     *
     * @precondition curToken is the first token of the source
     * @postcondition all top-level statements parsed; period consumed
     * @return a Block wrapping all top-level statements
     * @throws ScanErrorException if the scanner fails
     */
    public Block parseProgram() throws ScanErrorException
    {
        List<Statement> stmts = parseStmtList();
        if (curToken.equals("."))
        {
            eat(".");
        }
        return new Block(stmts);
    }

    /**
     * Parses the stmtList non-terminal using the parse table.
     * <pre>
     *   TABLE[stmtList][t in FIRST(stmt)] = stmtList -&gt; stmt stmtList
     *   TABLE[stmtList][t in {., EOF, END}] = stmtList -&gt; epsilon
     * </pre>
     *
     * @precondition curToken is the first token of a statement or
     *               a token in FOLLOW(stmtList)
     * @postcondition all statements in the list are consumed
     * @return a list of parsed Statement nodes
     * @throws ScanErrorException if the scanner fails
     */
    private List<Statement> parseStmtList() throws ScanErrorException
    {
        List<Statement> stmts = new ArrayList<Statement>();
        int prod = lookupTable("stmtList");
        while (prod == PROD_STMTLIST_STMT)
        {
            stmts.add(parseStatement());
            prod = lookupTable("stmtList");
        }
        // prod == PROD_STMTLIST_EPS: do nothing (epsilon)
        return stmts;
    }

    /**
     * Parses one statement using the parse table to select which
     * production to apply based on the current lookahead.
     * <pre>
     *   TABLE[stmt][WRITELN]  = stmt -&gt; WRITELN ( expr ) ;
     *   TABLE[stmt][READLN]   = stmt -&gt; READLN ( ID ) ;
     *   TABLE[stmt][IF]       = stmt -&gt; IF cond THEN stmt elsePart
     *   TABLE[stmt][WHILE]    = stmt -&gt; WHILE cond DO stmt
     *   TABLE[stmt][FOR]      = stmt -&gt; FOR ID := expr TO expr DO stmt
     *   TABLE[stmt][REPEAT]   = stmt -&gt; REPEAT stmt UNTIL cond ;
     *   TABLE[stmt][BREAK]    = stmt -&gt; BREAK ;
     *   TABLE[stmt][CONTINUE] = stmt -&gt; CONTINUE ;
     *   TABLE[stmt][BEGIN]    = stmt -&gt; BEGIN stmtList END ;
     *   TABLE[stmt][ID]       = stmt -&gt; ID := expr ;
     * </pre>
     *
     * @precondition curToken starts a valid statement
     * @postcondition the full statement (including trailing ;) is consumed
     * @return the Statement AST node
     * @throws ScanErrorException if the scanner fails
     */
    public Statement parseStatement() throws ScanErrorException
    {
        int prod = lookupTable("stmt");
        Statement result;
        switch (prod)
        {
            case PROD_STMT_WRITELN:
                eat("WRITELN");
                eat("(");
                Expression writelnExpr = parseExpr();
                eat(")");
                eat(";");
                result = new Writeln(writelnExpr);
                break;

            case PROD_STMT_READLN:
                eat("READLN");
                eat("(");
                if (!isId(curToken))
                {
                    throw new IllegalArgumentException(
                        "READLN expects identifier, got: " + curToken);
                }
                String readVar = curToken;
                eat(readVar);
                eat(")");
                eat(";");
                result = new Readln(readVar);
                break;

            case PROD_STMT_IF:
                eat("IF");
                Condition ifCond = parseCondition();
                eat("THEN");
                Statement thenStmt = parseStatement();
                Statement elseStmt = parseElsePart();
                result = new If(ifCond, thenStmt, elseStmt);
                break;

            case PROD_STMT_WHILE:
                eat("WHILE");
                Condition whileCond = parseCondition();
                eat("DO");
                Statement whileBody = parseStatement();
                result = new While(whileCond, whileBody);
                break;

            case PROD_STMT_FOR:
                eat("FOR");
                if (!isId(curToken))
                {
                    throw new IllegalArgumentException(
                        "FOR expects identifier, got: " + curToken);
                }
                String loopVar = curToken;
                eat(loopVar);
                eat(":=");
                Expression forStart = parseExpr();
                eat("TO");
                Expression forEnd = parseExpr();
                eat("DO");
                Statement forBody = parseStatement();
                result = new For(loopVar, forStart, forEnd, forBody);
                break;

            case PROD_STMT_REPEAT:
                eat("REPEAT");
                Statement repeatBody = parseStatement();
                eat("UNTIL");
                Condition repeatCond = parseCondition();
                eat(";");
                result = new RepeatUntil(repeatBody, repeatCond);
                break;

            case PROD_STMT_BREAK:
                eat("BREAK");
                eat(";");
                result = new BreakStmt();
                break;

            case PROD_STMT_CONTINUE:
                eat("CONTINUE");
                eat(";");
                result = new ContinueStmt();
                break;

            case PROD_STMT_BEGIN:
                eat("BEGIN");
                List<Statement> blockStmts = parseStmtList();
                eat("END");
                eat(";");
                result = new Block(blockStmts);
                break;

            case PROD_STMT_ASSIGN:
                String varName = curToken;
                eat(varName);
                eat(":=");
                Expression assignExpr = parseExpr();
                eat(";");
                result = new Assignment(varName, assignExpr);
                break;

            default:
                throw new IllegalArgumentException(
                    "unexpected production " + prod
                    + " for stmt at token '" + curToken + "'");
        }
        return result;
    }

    /**
     * Parses the elsePart non-terminal using the parse table.
     * <pre>
     *   TABLE[elsePart][ELSE]            = elsePart -&gt; ELSE stmt
     *   TABLE[elsePart][t in FOLLOW(stmt)] = elsePart -&gt; epsilon
     * </pre>
     * Returns null when the epsilon production is selected.
     *
     * @precondition curToken is ELSE or in FOLLOW(stmt)
     * @postcondition ELSE branch consumed if present
     * @return the else-branch Statement, or null if none
     * @throws ScanErrorException if the scanner fails
     */
    private Statement parseElsePart() throws ScanErrorException
    {
        int prod = lookupTable("elsePart");
        if (prod == PROD_ELSE_STMT)
        {
            eat("ELSE");
            return parseStatement();
        }
        // PROD_ELSE_EPS: no else branch
        return null;
    }

    /**
     * Parses a condition: cond -&gt; expr relop expr.
     * The relop is determined directly from the current token after
     * parsing the left-hand expression.
     *
     * @precondition curToken starts a valid expression
     * @postcondition left expr, relop, and right expr are consumed
     * @return a Condition node
     * @throws ScanErrorException if the scanner fails
     */
    public Condition parseCondition() throws ScanErrorException
    {
        Expression left = parseExpr();
        if (!isRelop(curToken))
        {
            throw new IllegalArgumentException(
                "Expected relational operator, got: " + curToken);
        }
        String relop = curToken;
        eat(relop);
        Expression right = parseExpr();
        return new Condition(left, relop, right);
    }

    /**
     * Parses an expression using the parse table.
     * <pre>
     *   TABLE[expr][t in {(, -, ID, NUMBER}] = expr -&gt; term exprTail
     * </pre>
     * The exprTail handles left-associative addition and subtraction
     * by threading the left operand through as an inherited attribute.
     *
     * @precondition curToken starts a valid expression
     * @postcondition all tokens in the expression are consumed
     * @return the Expression AST node
     * @throws ScanErrorException if the scanner fails
     */
    public Expression parseExpr() throws ScanErrorException
    {
        lookupTable("expr"); // validates the table entry exists
        Expression left = parseTerm();
        return parseExprTail(left);
    }

    /**
     * Parses the exprTail non-terminal using the parse table, threading
     * the left operand for left-associativity.
     * <pre>
     *   TABLE[exprTail][+]               = exprTail -&gt; + term exprTail
     *   TABLE[exprTail][-]               = exprTail -&gt; - term exprTail
     *   TABLE[exprTail][t in FOLLOW(expr)] = exprTail -&gt; epsilon
     * </pre>
     *
     * @param left the accumulated left-hand expression
     * @precondition left is not null; curToken is + , - , or in FOLLOW
     * @postcondition remaining +/- terms are consumed
     * @return the complete expression with left-associative operators
     * @throws ScanErrorException if the scanner fails
     */
    private Expression parseExprTail(Expression left)
        throws ScanErrorException
    {
        int prod = lookupTable("exprTail");
        switch (prod)
        {
            case PROD_EXPRTAIL_PLUS:
                eat("+");
                Expression rightPlus = parseTerm();
                return parseExprTail(new BinOp("+", left, rightPlus));

            case PROD_EXPRTAIL_MINUS:
                eat("-");
                Expression rightMinus = parseTerm();
                return parseExprTail(new BinOp("-", left, rightMinus));

            default:
                // PROD_EXPRTAIL_EPS: return accumulated expression
                return left;
        }
    }

    /**
     * Parses a term using the parse table.
     * <pre>
     *   TABLE[term][t in {(, -, ID, NUMBER}] = term -&gt; factor termTail
     * </pre>
     *
     * @precondition curToken starts a valid factor
     * @postcondition all tokens in the term are consumed
     * @return the Expression AST node
     * @throws ScanErrorException if the scanner fails
     */
    private Expression parseTerm() throws ScanErrorException
    {
        lookupTable("term"); // validates the table entry exists
        Expression left = parseFactor();
        return parseTermTail(left);
    }

    /**
     * Parses the termTail non-terminal using the parse table, threading
     * the left operand for left-associativity.
     * <pre>
     *   TABLE[termTail][*]               = termTail -&gt; * factor termTail
     *   TABLE[termTail][/]               = termTail -&gt; / factor termTail
     *   TABLE[termTail][mod]             = termTail -&gt; mod factor termTail
     *   TABLE[termTail][t in FOLLOW(term)] = termTail -&gt; epsilon
     * </pre>
     *
     * @param left the accumulated left-hand expression
     * @precondition left is not null; curToken is *, /, mod, or in FOLLOW
     * @postcondition remaining * / mod factors are consumed
     * @return the complete term with left-associative operators
     * @throws ScanErrorException if the scanner fails
     */
    private Expression parseTermTail(Expression left)
        throws ScanErrorException
    {
        int prod = lookupTable("termTail");
        switch (prod)
        {
            case PROD_TERMTAIL_STAR:
                eat("*");
                Expression rightStar = parseFactor();
                return parseTermTail(new BinOp("*", left, rightStar));

            case PROD_TERMTAIL_SLASH:
                eat("/");
                Expression rightSlash = parseFactor();
                return parseTermTail(new BinOp("/", left, rightSlash));

            case PROD_TERMTAIL_MOD:
                eat("mod");
                Expression rightMod = parseFactor();
                return parseTermTail(new BinOp("mod", left, rightMod));

            default:
                // PROD_TERMTAIL_EPS: return accumulated term
                return left;
        }
    }

    /**
     * Parses a factor using the parse table.
     * <pre>
     *   TABLE[factor][(]      = factor -&gt; ( expr )
     *   TABLE[factor][-]      = factor -&gt; - factor
     *   TABLE[factor][ID]     = factor -&gt; ID
     *   TABLE[factor][NUMBER] = factor -&gt; NUMBER
     * </pre>
     *
     * @precondition curToken starts a valid factor
     * @postcondition all tokens making up the factor are consumed
     * @return the Expression AST node for this factor
     * @throws ScanErrorException if the scanner fails
     */
    private Expression parseFactor() throws ScanErrorException
    {
        int prod = lookupTable("factor");
        switch (prod)
        {
            case PROD_FACTOR_PAREN:
                eat("(");
                Expression inner = parseExpr();
                eat(")");
                return inner;

            case PROD_FACTOR_NEG:
                eat("-");
                return new BinOp("-", new ast.Number(0), parseFactor());

            case PROD_FACTOR_ID:
                String name = curToken;
                eat(name);
                return new Variable(name);

            case PROD_FACTOR_NUM:
                int value = Integer.parseInt(curToken);
                eat(curToken);
                return new ast.Number(value);

            default:
                throw new IllegalArgumentException(
                    "unexpected production " + prod
                    + " for factor at token '" + curToken + "'");
        }
    }

    // ------------------------------------------------------------------
    // Program execution
    // ------------------------------------------------------------------

    /**
     * Convenience method: parses the program and immediately runs it
     * in a fresh environment, just like the recursive descent parser.
     *
     * @precondition the parser is at the start of a valid program
     * @postcondition the program has been fully parsed and executed
     * @throws ScanErrorException if the scanner runs into trouble
     */
    public void runProgram() throws ScanErrorException
    {
        Block program = parseProgram();
        program.exec(new Environment());
    }

    /**
     * Runs the LL(1) parser on source files; uses the default test
     * list when args is empty. Demonstrates that this table-driven
     * parser produces the same output as the recursive descent parser.
     *
     * @param args zero or more paths to source files; empty means
     *             run the built-in default suite
     * @precondition when args is non-empty, each path is a valid file
     * @postcondition each file is parsed and executed, or an error
     *                message is printed
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
                "parser/parserTestRepeatBreakContinue.txt"
            };
        }
        for (String path : files)
        {
            System.out.println("=== LL(1) Parser: " + path + " ===");
            try
            {
                FileInputStream in = new FileInputStream(path);
                scanner.Scanner scan = new scanner.Scanner(in);
                LL1Parser p = new LL1Parser(scan);
                p.runProgram();
                System.out.println("LL(1) parser completed successfully.");
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
