package edu.gatech.fallback;
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
        isFunction=true;
    }

    public Binding(String name, DeclaredType type, String scope) {

        this.name = name;
        this.type = type;
        this.scope = scope;
        isFunction=false;
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
}