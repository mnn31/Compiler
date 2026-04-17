package ast;

import java.util.List;
import environment.Environment;

/**
 * A procedure declaration: a name, an ordered list of parameter names, and
 * a body statement. Executing a ProcedureDeclaration simply registers it
 * with the environment's procedure table; the body runs later, when the
 * procedure is called.
 *
 * @author Manan Gupta
 * @version 2026-04-15
 */
public class ProcedureDeclaration extends Statement
{
    private final String name;
    private final List<String> params;
    private final Statement body;

    /**
     * Creates a procedure declaration node.
     *
     * @param name the procedure's identifier
     * @param params ordered list of parameter names (may be empty)
     * @param body the statement to execute when the procedure is called
     * @precondition name != null, params != null, body != null
     */
    public ProcedureDeclaration(String name, List<String> params, Statement body)
    {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    /**
     * Returns this procedure's name.
     *
     * @return the identifier used to call this procedure
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the ordered list of parameter names.
     *
     * @return the parameter names (possibly empty, never null)
     */
    public List<String> getParams()
    {
        return params;
    }

    /**
     * Returns the procedure body statement.
     *
     * @return the body to execute on each call
     */
    public Statement getBody()
    {
        return body;
    }

    /**
     * Registers this declaration in the (global) environment.
     *
     * @param env any environment; the declaration is stored globally
     * @precondition env != null
     * @postcondition env.getProcedure(name) returns this declaration
     */
    @Override
    public void exec(Environment env)
    {
        env.setProcedure(name, this);
    }
}
