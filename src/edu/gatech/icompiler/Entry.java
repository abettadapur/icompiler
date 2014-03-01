package edu.gatech.icompiler;
import java.util.List;

public  class Entry{

    private String name;
    private DeclaredType type;
    private int dimensions;
    private DeclaredType contentType;//arrays only
    //how to do declaring procedure? is it even necessary?
    private boolean referenceP;
    private List<DeclaredType> parameters;

    //For single variables
    public static Entry getVariableInstance(String name, DeclaredType type){
        return new Entry(name, type, 0, null, false, null );
    }

    //For arrays
    public static Entry getArrayInstance(String name, DeclaredType type, int dimensions, DeclaredType contentType){
        return new Entry(name, type, dimensions, contentType, true, null );
    }

    //For functions
    public static Entry getFunctionInstance(String name, DeclaredType type, List<DeclaredType> parameters){
        return new Entry(name, type, 0, null, false, parameters );
    }

    private Entry(String name, DeclaredType type,
                  int dimensions, DeclaredType contentType, boolean referenceP, List<DeclaredType> parameters){
        this.name = name;
        this.type = type;
        this.dimensions = dimensions;
        this.referenceP = referenceP;
        this.parameters = parameters;
    }

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