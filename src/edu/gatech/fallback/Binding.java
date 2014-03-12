package edu.gatech.fallback;
import java.util.List;

public  class Binding
{
    private String name;
    private String type;
    private String scope;
    private String isFunction;
    private List<String> params;

    public String toString(){
        return "["+name + " " +type+"]";
    }
    private class DeclaredType{
        String typeName;

        public DeclaredType(String typeName){
            this.typeName = typeName;
        }

        public int hashCode(){
            return typeName.hashCode();
        }

        public boolean equals(Object other ){
            if(null == other) return false;
            if(this == other) return true;
            if(!(other instanceof DeclaredType)) return false;
            DeclaredType temp = (DeclaredType) other;
            return temp.typeName.equals(typeName);
        }
    }
}