package edu.gatech.fallback;

/**
 * Created by Alex on 3/12/14.
 */
public class DeclaredType
{
    public static final DeclaredType integer = new DeclaredType("int" , "");
    public static final DeclaredType str = new DeclaredType("string","");
    private String typeName;
    private String scope;
    private boolean isArray;
    private int dimensions;
    private DeclaredType container;

    public DeclaredType(String typeName, String scope)
    {
        this.isArray=false;
        this.typeName = typeName;
        this.scope = scope;
        dimensions=0;
        container=null;
    }
    public DeclaredType(int dimensions, DeclaredType container, String scope)
    {
        this.scope=scope;
        this.isArray=true;
        this.dimensions=dimensions;
        this.container=container;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isArray() {
        return isArray;
    }

    public int getDimensions() {
        return dimensions;
    }

    public DeclaredType getContainer() {
        return container;
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
