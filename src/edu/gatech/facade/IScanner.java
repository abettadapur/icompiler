package edu.gatech.facade;

import edu.gatech.icompiler.Entity;
import edu.gatech.icompiler.TokenType;
/**
 * Created by Stefano on 1/31/14.
 */

public interface IScanner {

    public Entity<TokenType> next();

    public boolean hasNext();

}
