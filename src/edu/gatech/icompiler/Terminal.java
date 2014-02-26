package edu.gatech.icompiler;

/**
 * Created by Alex on 2/26/14.
 */
public class Terminal implements Type
{
    public String getContent() {
        return content;
    }

    private final String content;

    public Terminal(String content) {
        this.content = content;
    }

    @Override
    public boolean isToken() {
        return false;
    }
    public boolean isTerminal()
    {
        return true;
    }
}
