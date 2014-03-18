package edu.gatech.icompiler;

import java.util.ArrayList;
import java.util.List;

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
    private List<Integer> dimensions;
    private DeclaredType container;

    public DeclaredType(String typeName, String scope)
    {
        this.isArray=false;
        this.typeName = typeName;
        this.scope = scope;
        dimensions=new ArrayList<>();
        container=null;
    }
    public DeclaredType(List<Integer> dimensions, DeclaredType container, String scope)
    {
        this.typeName="";
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

    public int getDimensionCount() {
        return dimensions.size();
    }

    public int getDimensionX(int X)
    {
        if(X>=dimensions.size())
            return -1;
        else
            return dimensions.get(X);
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
    public String toString()
    {
        return typeName;

    }
    public String toVerboseString()
    {
        StringBuilder sb = new StringBuilder();
        if(isArray)
        {
            sb.append("Array of "+container+" Size: ");
            for(int i:dimensions)
                sb.append("["+i+"]");

        }
        else
        {
            sb.append(typeName);
            //sb.append(" Context: "+(scope.equals("")?"global":scope));
        }
        return sb.toString();
    }


}
