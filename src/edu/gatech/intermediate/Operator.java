package edu.gatech.intermediate;

import edu.gatech.icompiler.TokenType;

/**
 * Created by Stefano on 3/10/14.
 */
public enum Operator {

    ASSIGN, ADD, SUB, MULT, DIV, AND, OR, GOTO, BREQ,
    BRNEQ, BRLT, BRGT, BRGEQ, BRLEQ, RETURN, CALL, CALLR,
    ARRAY_STORE, ARRAY_LOAD, UNSUPPORTED;

    //TODO: CALL, CALLR, ARRAY_STORE, ARRAY_LOAD

    public static Operator getFromTokenTypes(TokenType type){

        if(type.equals(TokenType.LESSER))
            return BRLT;

        if(type.equals(TokenType.GREATER))
            return BRGT;

        if( null != type)
            for(Operator t : Operator.values())
                if(type.name().equals(t.name()) || ("BR" + type.name()).equals(t.name()) )
                    return t;

        return UNSUPPORTED;
    }
}
