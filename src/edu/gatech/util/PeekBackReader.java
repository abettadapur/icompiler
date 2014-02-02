package edu.gatech.util;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

/**
 * Created by Alex on 2/2/14.
 */
public class PeekBackReader extends PushbackReader
{
    public PeekBackReader(Reader r)
    {
        super(r);
    }

    public boolean hasNext() throws IOException
    {
        char c = (char)read();
        if(c==Character.MAX_VALUE)
        {
            return false;
        }
        else
        {
            unread(c);
            return true;
        }

    }
    public char peek() throws IOException
    {
        char c = (char)read();
        if(c!=Character.MAX_VALUE)
            unread(c);
        return c;
    }

}
