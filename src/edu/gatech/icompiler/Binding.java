package edu.gatech.icompiler;
import java.util.List;

public  class Binding
{
    private String name;
    private DeclaredType type;
    private String scope;
    private boolean isFunction;
    private List<DeclaredType> params;

    public Binding(String name, DeclaredType type, String scope, List<DeclaredType> params) {
        this.name = name;
        this.type = type;
        this.scope = scope;
        this.params = params;
        isFunction=params !=null;
    }

    public Binding(String name, DeclaredType type, String scope) {
        this(name, type, scope, null);
    }

    public String getName() {
        return name;
    }

    public DeclaredType getType() {
        return type;
    }

    public String getScope() {
        return scope;
    }

    public boolean isFunction() {
        return isFunction;
    }

    public List<DeclaredType> getParams() {
        return params;
    }

    public boolean equals(Object other)
    {
        if(null == other) return false;
        if(this == other) return true;
        if(!(other instanceof DeclaredType)) return false;
        Binding temp = (Binding) other;
        return temp.name.equals(name)&&temp.scope.equals(scope);
    }

    public String toString()
    {
        if(isFunction())
        {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append("(");
            for(DeclaredType t: params)
            {
                if(t.isArray())
                    sb.append(t.toVerboseString()+",");
                else
                    sb.append(t+",");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append(") : ");
            sb.append(type);
            return sb.toString();
        }
        else
        {
            return name+" "+type.toVerboseString()+" Context: "+(scope.equals("")?"global":scope);
        }
    }

}