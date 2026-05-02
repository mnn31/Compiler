package emitter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Wraps a PrintWriter to emit MIPS assembly to a file. Indents non-label lines
 * with a single tab and provides helpers for the compiler-wide conventions of
 * pushing/popping $v0 and generating unique label IDs.
 *
 * @author Manan Gupta
 * @version 2026-05-02
 */
public class Emitter
{
    private PrintWriter out;
    private int labelCount;

    /**
     * Opens outputFileName for writing and gets the label counter ready.
     *
     * @param outputFileName path of the file to create or overwrite
     * @precondition outputFileName is a writable path
     * @postcondition out is open for writing; labelCount is 0
     */
    public Emitter(String outputFileName)
    {
        try
        {
            out = new PrintWriter(new FileWriter(outputFileName), true);
            labelCount = 0;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes one line of code, prepending a tab unless code is a label.
     *
     * @param code the line to write (without trailing newline)
     * @precondition code != null
     * @postcondition the line is flushed to the output file
     */
    public void emit(String code)
    {
        if (!code.endsWith(":"))
        {
            code = "\t" + code;
        }
        out.println(code);
    }

    /**
     * Pushes the value in reg onto the runtime stack by decrementing $sp and
     * storing the word at the new top of stack.
     *
     * @param reg name of the source register, e.g. "$v0"
     * @precondition reg is a valid MIPS register name
     * @postcondition two MIPS instructions are emitted that grow the stack by 4 bytes
     */
    public void emitPush(String reg)
    {
        emit("subu $sp $sp 4");
        emit("sw " + reg + " ($sp)\t# push " + reg);
    }

    /**
     * Pops the top word of the runtime stack into reg by loading from $sp and
     * incrementing $sp.
     *
     * @param reg name of the destination register, e.g. "$t0"
     * @precondition reg is a valid MIPS register name; the stack is non-empty
     * @postcondition two MIPS instructions are emitted that shrink the stack by 4 bytes
     */
    public void emitPop(String reg)
    {
        emit("lw " + reg + " ($sp)\t# pop into " + reg);
        emit("addu $sp $sp 4");
    }

    /**
     * Returns a fresh label ID (1 on first call, 2 on the second, ...).
     * Compile methods append this ID to a label prefix to keep label names unique.
     *
     * @return the next unused label ID
     * @postcondition the internal counter is incremented
     */
    public int nextLabelID()
    {
        labelCount++;
        return labelCount;
    }

    /**
     * Closes the underlying PrintWriter. Should be called once after all emits.
     *
     * @postcondition no further writes are possible through this Emitter
     */
    public void close()
    {
        out.close();
    }
}
