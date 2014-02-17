package edu.gatech.icompiler;

/**
 * Created by Alex on 2/15/14.
 */
public class CompileError
{
    private ErrorType type;
    private String text;
    private int lineNumber;

    public CompileError(ErrorType type, String text, int lineNumber) {
        this.type = type;
        this.text = text;
        this.lineNumber = lineNumber;
    }

    public ErrorType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String toString()
    {
        return type.name()+" at "+lineNumber+": "+text;
    }
}
