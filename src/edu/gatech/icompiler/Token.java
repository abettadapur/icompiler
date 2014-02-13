package edu.gatech.icompiler;

/**
 * Created by Stefano on 2/12/14.
 */
public class Token {


    public final TokenType TYPE;
    public final String TOKEN_CONTENT;

    public Token(TokenType type, String content){
        TYPE = type;
        TOKEN_CONTENT  = content;
    }

    public boolean isToken(){
        return TYPE.getClass() == TokenType.class;
    }

    @Override
    public int hashCode(){
        return (TYPE.hashCode()+ TOKEN_CONTENT.hashCode())*(TYPE.hashCode()
                + TOKEN_CONTENT.hashCode() + 1)/2 + TOKEN_CONTENT.hashCode();
    }

    @Override
    public boolean equals(Object other){

        if(null == other) return false;
        if(this == other) return true;
        if(! (other instanceof Token)) return false;
        Token temp = (Token) other;
        return TOKEN_CONTENT.equals(temp.TOKEN_CONTENT) &&
                TYPE.equals(temp.TYPE);

    }

    @Override
    public String toString(){
        return "[" + TYPE.name() + ", " + TOKEN_CONTENT + "]";
    }

}
