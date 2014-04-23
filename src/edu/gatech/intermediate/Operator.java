package edu.gatech.intermediate;

import edu.gatech.icompiler.Token;
import edu.gatech.icompiler.TokenType;

/**
 * Created by Stefano on 3/10/14.
 */
public enum Operator {

    ASSIGN, ADD, SUB, MULT, DIV, AND, OR, GOTO, BREQ,
    BRNEQ, BRLT, BRGT, BRGEQ, BRLEQ, RETURN, CALL, CALLR,
    ARRAY_STORE, ARRAY_LOAD, LOAD, STORE, UNSUPPORTED,END;

    //TODO: CALL, CALLR, ARRAY_STORE, ARRAY_LOAD

    public static Operator getFromString(String foo){
        Operator temp = UNSUPPORTED;

        switch(foo){
            case "+": temp = ADD; break;
            case "-": temp = SUB; break;
            case "*": temp = MULT; break;
            case "/": temp = DIV; break;
            case "&": temp = AND; break;
            case "|": temp = OR; break;
            case ":=": temp = ASSIGN; break;
            default: temp = UNSUPPORTED; break;
        }

        return temp;
    }

}
