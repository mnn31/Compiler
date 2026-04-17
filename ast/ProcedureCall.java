package ast;

import java.util.List;
import environment.Environment;

/**
 * A procedure call expression: looks up the matching declaration, evaluates
 * the arguments in the caller's environment, binds them to the declared
 * parameters inside a fresh child environment hanging off the global one,
 * runs the body, and returns the value of the variable sharing the
 * procedure's name (Pascal-style return). Defaults to 0 if the body never
 * assigns that variable.
 *
 * @author Manan Gupta
 * @version 2026-04-15
 */
public class ProcedureCall extends Expression
{
    private final String name;
    private final List<Expression> args;

    /**
     * Creates a procedure call node.
     *
     * @param name the procedure to call
     * @param args ordered list of argument expressions (may be empty)
     * @precondition name != null, args != null
     */
    public ProcedureCall(String name, List<Expression> args)
    {
        this.name = name;
        this.args = args;
    }

    /**
     * Executes the procedure call and returns its value.
     * Steps: look up the declaration, evaluate each argument in env,
     * create a child environment off the global one, bind parameters
     * and an implicit return variable (named after the procedure) to 0,
     * run the body, and return the value of that return variable.
     *
     * @param env the caller's environment; used only to evaluate arguments
     *            and to reach the global environment
     * @return the value of the variable named after the procedure at the
     *         point the body finishes, or 0 if never set
     * @throws IllegalArgumentException if the procedure isn't declared,
     *         or the argument count doesn't match the parameter count
     */
    @Override
    public int eval(Environment env)
    {
        ProcedureDeclaration decl = env.getProcedure(name);
        List<String> params = decl.getParams();
        if (params.size() != args.size())
        {
            throw new IllegalArgumentException(
                "procedure '" + name + "' expects " + params.size()
                + " argument(s) but got " + args.size());
        }
        // Evaluate arguments in the caller's environment before building the new one.
        int[] argValues = new int[args.size()];
        for (int i = 0; i < args.size(); i++)
        {
            argValues[i] = args.get(i).eval(env);
        }
        // New environment hangs off the global env, per the scoping rules.
        Environment callEnv = new Environment(env.globalScope());
        callEnv.declareVariable(name, 0);
        for (int i = 0; i < params.size(); i++)
        {
            callEnv.declareVariable(params.get(i), argValues[i]);
        }
        decl.getBody().exec(callEnv);
        return callEnv.getVariable(name);
    }

}
