package edu.gatech.fallback;

import edu.gatech.facade.ITable;
import edu.gatech.icompiler.*;
import edu.gatech.util.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Alex on 3/12/14.
 */
public class SymbolTable implements ITable
{
    HashMap<String, Binding> identifiers;
    HashMap<String, DeclaredType> typeLookup;
    HashMap<DeclaredType, DeclaredType> typeMapping;

    public SymbolTable()
    {
        identifiers = new HashMap<>();
        typeLookup = new HashMap<>();
        typeLookup.put("int", DeclaredType.integer);
        typeLookup.put("string", DeclaredType.str);
        typeMapping = new HashMap<>();
    }

    //TODO: Figure out arrays
    public void populateTypes(Node<Type> subRoot)
    {
        //subRoot is of type TypeDeclarationList
        for(Node<Type> currentNode:subRoot)
        {
            if(currentNode.getData()== RuleType.TYPE_DECLARATION)
            {
                String alias = ((Terminal)currentNode.getChildren().get(1).getData()).getContent();

                Node<Type> typeNode = currentNode.getChildren().get(3);

                if(typeNode.getChildren().get(0).getData() == TokenType.ARRAY)
                {
                    //this is a new type, no mapping needed
                    int dimension = 0;
                    for(Node<Type> node: typeNode)
                    {
                        if(node.getData()==TokenType.LBRACK)
                        {
                            dimension++;
                        }
                    }
                    String containerId = ((Terminal)typeNode.getChildren().get(6).getChildren().get(0).getChildren().get(0).getData()).getContent();
                    if(!typeLookup.containsKey(containerId))
                    {
                        //undeclared type
                    }
                    else
                    {
                        DeclaredType arrayType = new DeclaredType(dimension, typeLookup.get(containerId));
                        if(!addType(alias, arrayType))
                        {
                            //error alias already delclared
                        }
                    }

                }
                else
                {
                    String id = ((Terminal)typeNode.getChildren().get(0).getChildren().get(0).getChildren().get(0).getData()).getContent();
                    if(!typeLookup.containsKey(id))
                    {
                        //error undeclared type
                    }
                    else
                    {
                        DeclaredType oldType = typeLookup.get(id);
                        DeclaredType newType = new DeclaredType(alias);
                        if(!addTypeMap(newType, oldType))
                        {
                            //error
                        }

                    }

                }

            }
        }

    }

    public void populateVars(Node<Type> subRoot)
    {
        //subRoot is a var_decl-list
        for(Node<Type> currentNode:subRoot)
        {
            if(currentNode.getData()==RuleType.VAR_DECLARATION)
            {
                //new variable declaration
                Node<Type> id_list = currentNode.getChildren().get(1);
                Node<Type> type_id = currentNode.getChildren().get(3);
                String typeid = ((Terminal)type_id.getChildren().get(0).getChildren().get(0).getData()).getContent();   //type-id->id->terminal
                if(!findType(typeid).equals(""))
                {
                    List<String> ids = new ArrayList<>();
                    for(Node<Type> node: id_list)
                    {
                        if(node.getData()==TokenType.ID)
                        {
                            ids.add(((Terminal)node.getChildren().get(0).getData()).getContent()); //add id to list to add
                        }
                    }
                    for(String id:ids)
                    {
                       // Binding entry = new Binding()
                    }
                }

            }
        }
    }
    public void populateFunctions(Node<Type> subRoot)
    {
        //sub root is a funct declaration
    }

    public boolean addIdentifier(String name, Binding bindings)
    {
        //check bindings are valid first
        identifiers.put(name,bindings);
        return false;
    }
    public boolean addTypeMap(DeclaredType alias, DeclaredType aliased)
    {
        if(typeMapping.containsKey(alias))
            return false;
        typeMapping.put(alias, aliased);
        return true;
    }
    public boolean addType(String id, DeclaredType type)
    {
        if(typeLookup.containsKey(id))
            return false;
        typeLookup.put(id, type);
    }


    @Override
    public List<Binding> find(String name) {
        return null;
    }

    @Override
    public Binding findByNameScope(String name, String scope) {
        return null;
    }

    public DeclaredType findType(String name)
    {
        return typeLookup.get(name);
    }
    public DeclaredType findPrimitive(String name,String scope)
    {
        while(true)
        {
            DeclaredType type = findType(name);
            if(type==null)
                return null;
            if(type.isArray())
                return type;
            DeclaredType mapping = typeMapping.get(type);
            if(mapping.equals(DeclaredType.integer)|| mapping.equals(DeclaredType.str))
                return mapping;
        }


    }
}
