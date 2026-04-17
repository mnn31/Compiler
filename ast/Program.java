package ast;

import java.util.List;
import environment.Environment;

/**
 * Root of the AST for a full source file: a (possibly empty) list of
 * procedure declarations followed by a single main statement (usually a
 * BEGIN/END block). Executing a Program first registers every procedure
 * in the environment, then runs the main statement.
 *
 * @author Manan Gupta
 * @version 2026-04-15
 */
public class Program extends Statement
{
    private final List<ProcedureDeclaration> procedures;
    private final Statement main;

    /**
     * Creates a Program node.
     *
     * @param procedures the procedure declarations, in source order
     * @param main the main statement to run after declarations are registered
     * @precondition procedures != null (may be empty), main != null
     */
    public Program(List<ProcedureDeclaration> procedures, Statement main)
    {
        this.procedures = procedures;
        this.main = main;
    }

    /**
     * Registers each procedure, then runs the main statement.
     *
     * @param env the (global) environment in which to execute
     * @precondition env != null and is the global environment
     * @postcondition every declared procedure is registered in env;
     *                the main statement has fully executed
     */
    @Override
    public void exec(Environment env)
    {
        for (ProcedureDeclaration proc : procedures)
        {
            proc.exec(env);
        }
        main.exec(env);
    }
}
