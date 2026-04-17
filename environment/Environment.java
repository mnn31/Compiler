package environment;

import java.util.HashMap;
import java.util.Map;
import ast.ProcedureDeclaration;

/**
 * Stores variable bindings and procedure declarations for the interpreter.
 * Supports nested (linked) scopes via a parent reference: the global environment
 * has a null parent, and each procedure call creates a child environment whose
 * parent is the global environment.
 *
 * <p>Procedures always live in the global (root) environment, so
 * {@link #getProcedure(String)} and {@link #setProcedure(String, ProcedureDeclaration)}
 * always walk up to the root. Variables have more nuanced scoping rules --
 * see the individual method docs Ms Datar!!
 *
 * @author Manan Gupta
 * @version 2026-04-15
 */
public class Environment
{
    private final Map<String, Integer> variables;
    private final Map<String, ProcedureDeclaration> procedures;
    private final Environment parent;

    /**
     * Makes a fresh empty global environment (no parent).
     *
     * @postcondition no variables or procedures are defined yet; parent is null
     */
    public Environment()
    {
        this(null);
    }

    /**
     * Makes a fresh child environment whose parent is the given environment.
     * If parent is null, this is the global environment.
     *
     * @param parent the enclosing environment, or null to create a global one
     * @postcondition no variables are defined in this new scope;
     *                procedure lookups will walk up to the global env
     */
    public Environment(Environment parent)
    {
        this.variables = new HashMap<String, Integer>();
        this.procedures = new HashMap<String, ProcedureDeclaration>();
        this.parent = parent;
    }

    /**
     * Walks up the parent chain to find the global (root) environment.
     *
     * @return the root environment (the one whose parent is null)
     * @postcondition the returned environment has parent == null
     */
    private Environment getGlobal()
    {
        Environment env = this;
        while (env.parent != null)
        {
            env = env.parent;
        }
        return env;
    }

    /**
     * Returns the global (root) environment reachable by walking parent
     * pointers. Useful for callers that need to create a new child
     * environment hanging directly off the global scope.
     *
     * @return the root environment in this chain
     */
    public Environment globalScope()
    {
        return getGlobal();
    }

    /**
     * Declares a variable with the given value <em>in this environment</em>.
     * Unlike setVariable, this never walks up to the parent -- it is how new
     * local bindings (e.g. procedure parameters) are introduced.
     *
     * @param variable name of the variable to declare locally
     * @param value the value to bind
     * @precondition variable != null
     * @postcondition this.variables contains variable -&gt; value
     */
    public void declareVariable(String variable, int value)
    {
        variables.put(variable, value);
    }

    /**
     * Sets a variable's value with scope-aware rules:
     * if the variable already exists in this environment, update it here;
     * otherwise, if it exists in the global environment, update it there;
     * otherwise, declare a fresh variable in this environment.
     *
     * @param variable name of the variable to set
     * @param value value to assign
     * @precondition variable != null
     * @postcondition the variable is bound to value somewhere up the chain
     */
    public void setVariable(String variable, int value)
    {
        if (variables.containsKey(variable))
        {
            variables.put(variable, value);
            return;
        }
        Environment global = getGlobal();
        if (global != this && global.variables.containsKey(variable))
        {
            global.variables.put(variable, value);
        }
        else
        {
            variables.put(variable, value);
        }
    }

    /**
     * Looks up a variable: checks this environment first, then walks up to
     * the parent chain. If the variable is not declared anywhere, returns 0
     * (matches the lab spec's fallback semantics).
     *
     * @param variable name of the variable to look up
     * @return the variable's current value, or 0 if it was never declared
     * @precondition variable != null
     * @postcondition no state is modified
     */
    public int getVariable(String variable)
    {
        if (variables.containsKey(variable))
        {
            return variables.get(variable);
        }
        if (parent != null)
        {
            Environment global = getGlobal();
            if (global.variables.containsKey(variable))
            {
                return global.variables.get(variable);
            }
            return 0;
        }
        return 0;
    }

    /**
     * Stores a procedure declaration in the global environment.
     *
     * @param name procedure name
     * @param proc the declaration to associate with name
     * @precondition name != null and proc != null
     * @postcondition the global environment's procedure table maps name -&gt; proc
     */
    public void setProcedure(String name, ProcedureDeclaration proc)
    {
        getGlobal().procedures.put(name, proc);
    }

    /**
     * Retrieves a procedure declaration from the global environment.
     *
     * @param name procedure name to look up
     * @return the declaration previously stored under name
     * @precondition setProcedure(name, ...) has been called
     * @throws IllegalArgumentException if no such procedure was declared
     */
    public ProcedureDeclaration getProcedure(String name)
    {
        Environment global = getGlobal();
        if (!global.procedures.containsKey(name))
        {
            throw new IllegalArgumentException(
                "procedure '" + name + "' hasn't been declared yet!");
        }
        return global.procedures.get(name);
    }
}
