package ast;

import java.util.List;
import emitter.Emitter;
import environment.Environment;

/**
 * Root of the AST for a full source file: a (possibly empty) list of global
 * variable names, a (possibly empty) list of procedure declarations, and a
 * single main statement (usually a BEGIN/END block). Executing a Program
 * declares each global variable to 0, registers every procedure, and then
 * runs the main statement.
 *
 * <p>Program is intentionally <em>not</em> a Statement -- it represents
 * the entire compilation unit, not a single executable line.
 *
 * @author Manan Gupta
 * @version 2026-05-02
 */
public class Program
{
    private final List<String> variables;
    private final List<ProcedureDeclaration> procedures;
    private final Statement main;

    /**
     * Creates a Program node.
     *
     * @param variables global variable names declared by VAR, in source order
     * @param procedures the procedure declarations, in source order
     * @param main the main statement to run after declarations are registered
     * @precondition all three lists are non-null (lists may be empty), main != null
     */
    public Program(List<String> variables, List<ProcedureDeclaration> procedures, Statement main)
    {
        this.variables = variables;
        this.procedures = procedures;
        this.main = main;
    }

    /**
     * Declares each global variable as 0, registers each procedure, then runs
     * the main statement.
     *
     * @param env the (global) environment in which to execute
     * @precondition env != null and is the global environment
     * @postcondition every declared variable and procedure is registered in env;
     *                the main statement has fully executed
     */
    public void exec(Environment env)
    {
        for (String name : variables)
        {
            env.declareVariable(name, 0);
        }
        for (ProcedureDeclaration proc : procedures)
        {
            proc.exec(env);
        }
        main.exec(env);
    }

    /**
     * Compiles the whole program to MIPS at outputFileName: emits a Javadoc
     * header, the .data section listing each global variable (with the
     * "var" prefix and a 0 default) plus a newline string for WRITELN, then
     * the .text section that calls main and exits cleanly via syscall 10.
     *
     * @param outputFileName path of the .asm file to create
     * @precondition outputFileName is a writable path ending in .asm/.s/.a
     * @postcondition a complete, runnable MIPS program is written to outputFileName
     */
    public void compile(String outputFileName)
    {
        Emitter e = new Emitter(outputFileName);
        e.emit("# /**");
        e.emit("#  * @author Manan Gupta");
        e.emit("#  * @version 2026-05-02");
        e.emit("#  */");
        e.emit(".data");
        for (String name : variables)
        {
            e.emit("var" + name + ":");
            e.emit(".word 0");
        }
        e.emit("newline:");
        e.emit(".asciiz \"\\n\"");
        e.emit(".text");
        e.emit(".globl main");
        e.emit("main:");
        main.compile(e);
        e.emit("li $v0 10\t# normal termination");
        e.emit("syscall");
        e.close();
    }
}
