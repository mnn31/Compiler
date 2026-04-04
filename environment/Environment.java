package environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Runtime symbol table mapping variable names to integer values during AST interpretation.
 *
 * @author Manan Gupta
 * @version 2026-03-25
 */
public class Environment
{
    private final Map<String, Integer> map;

    /**
     * Constructs an empty environment with no variables defined.
     *
     * @postcondition the symbol table has no bindings yet
     */
    public Environment()
    {
        map = new HashMap<String, Integer>();
    }

    /**
     * Binds or updates a variable to an integer value.
     *
     * @param variable the name of the variable to set
     * @param value    the integer value to associate with that name
     * @precondition variable is a non-null name string
     * @postcondition the given variable name maps to the given value in this environment
     */
    public void setVariable(String variable, int value)
    {
        map.put(variable, value);
    }

    /**
     * Looks up the current value of a variable.
     *
     * @param variable the name of the variable to read
     * @return the integer currently stored for that name
     * @precondition the variable has been set in this environment
     * @postcondition returns the value most recently stored for that variable name
     * @throws IllegalArgumentException if no value has been set for this name
     */
    public int getVariable(String variable)
    {
        if (!map.containsKey(variable))
        {
            throw new IllegalArgumentException("Undefined variable: " + variable);
        }
        return map.get(variable);
    }
}
