package edu.gatech.icompiler;

/**
 * Created by Stefano on 2/12/14.
 */
public abstract class Entity<T extends Enum> {

    public final T TYPE;
    public final String TOKEN_CONTENT;

    public Entity(T type, String content){
        TYPE = type;
        TOKEN_CONTENT  = content;
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
                TYPE.equals(temp.TOKEN_TYPE);

    }

    public String toString(){
        return "[" + TYPE.name() + ", " + TOKEN_CONTENT + "]";
    }
}
