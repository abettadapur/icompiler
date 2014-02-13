package edu.gatech.facade;

import edu.gatech.icompiler.Token;
import edu.gatech.icompiler.TokenType;
/**
 * Created by Stefano on 1/31/14.
 */

public interface IScanner {

    public Token next();

    public boolean hasNext();

    public Token peek();
}
