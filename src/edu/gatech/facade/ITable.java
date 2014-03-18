package edu.gatech.facade;

import edu.gatech.icompiler.Binding;
import edu.gatech.icompiler.DeclaredType;

import java.util.List;

/**
 * Created by Alex on 3/1/14.
 */
public interface ITable
{
    public List<Binding> find(String name);
    public Binding findByNameScope(String name, String scope);
    public DeclaredType findType(String id);
    public DeclaredType findPrimitive(String id,String scope);
    public DeclaredType findTypeMap(DeclaredType type);
}

