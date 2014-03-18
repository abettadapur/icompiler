package edu.gatech.facade;

import edu.gatech.icompiler.Token;
/**
 * Created by Stefano on 1/31/14.
 */

public interface IScanner {

    public Token next();

    public boolean hasNext();

    public Token peek();
}
