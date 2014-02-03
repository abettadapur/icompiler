package edu.gatech.icompiler;

/**
 * Created by Stefano on 2/2/14.
 */
public enum TokenType {

    COMMA, COLON, SEMI, LPAREN, RPAREN, LBRACK, RBRACK, LBRACE, RBRACE, PERIOD,
    PLUS, MINUS, MULT, DIV, EQ, NEQ, LESSER, GREATER, LEQ, GEQ, AND,
    OR, ASSIGN, ARRAY, BREAK, DO, ELSE, FOR, FUNC, IF, IN, LET, NIL, OF,
    THEN, TO, TYPE, VAR, WHILE, ID, INTLIT, STRLIT, ENDIF, ENDDO, BEGIN, END;

    public static TokenType getFromString(String foo){

        if( null != foo)
            for(TokenType t : TokenType.values())
                if(foo.equalsIgnoreCase(t.name()))
                    return t;

        return null;
    }
}
