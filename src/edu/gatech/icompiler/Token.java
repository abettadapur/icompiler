package edu.gatech.icompiler;

/**
 * Created by Stefano on 2/1/14.
 */
public class Token {

    public final TokenType TOKEN_TYPE;
    public final String TOKEN_CONTENT;

    public Token(TokenType type, String content){
        TOKEN_TYPE = type;
        TOKEN_CONTENT  = content;
    }

    @Override
    public int hashCode(){
        return (TOKEN_TYPE.hashCode()+ TOKEN_CONTENT.hashCode())*(TOKEN_TYPE.hashCode()
                + TOKEN_CONTENT.hashCode() + 1)/2 + TOKEN_CONTENT.hashCode();
    }

    @Override
    public boolean equals(Object other){
        if(null == other) return false;
        if(this == other) return true;
        if(! (other instanceof Token)) return false;
        Token temp = (Token) other;
        return TOKEN_CONTENT.equals(temp.TOKEN_CONTENT) &&
                TOKEN_TYPE.equals(temp.TOKEN_TYPE);

    }

    public String toString(){
        return "[" + TOKEN_TYPE.name() + ", " + TOKEN_CONTENT + "]";
    }

}
