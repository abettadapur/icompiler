package edu.gatech.icompiler;

import edu.gatech.facade.ITable;
import edu.gatech.util.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 3/12/14.
 */
public class SymbolTable implements ITable
{
    HashMap<String, List<Binding>> identifiers;
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

    public List<String> populateTable(Node<Type> declaration)
    {
        if(declaration.getData()==RuleType.DECLARATION_SEGMENT)
        {
            List<String> errors = new ArrayList<String>();
            errors.addAll(populateTypes(declaration.getChildren().get(0)));
            errors.addAll(populateVars(declaration.getChildren().get(1)));
            errors.addAll(populateFunctions(declaration.getChildren().get(2)));
            return errors;
        }
        else
            throw new IllegalArgumentException();
    }

    private List<String> populateTypes(Node<Type> subRoot)
    {
        List<String> errors = new ArrayList<String>();
        if(subRoot.getData()==RuleType.TYPE_DECLARATION_LIST)
        {
        //subRoot is of type TypeDeclarationList
            for(Node<Type> currentNode:subRoot)
            {
                if(currentNode.getData()== RuleType.TYPE_DECLARATION)
                {
                    String alias = ((Terminal)currentNode.getChildren().get(1).getChildren().get(0).getData()).getContent();

                    Node<Type> typeNode = currentNode.getChildren().get(3);

                    if(typeNode.getChildren().get(0).getData() == TokenType.ARRAY)
                    {
                        //this is a new type, no mapping needed
                        List<Integer> dimension = new ArrayList<>();
                        for(Node<Type> node: typeNode)
                        {
                            if(node.getData()==TokenType.INTLIT)
                            {
                                dimension.add(Integer.parseInt(((Terminal) node.getChildren().get(0).getData()).getContent()));
                            }

                        }
                        String containerId = ((Terminal)typeNode.getChildren().get(6).getChildren().get(0).getChildren().get(0).getData()).getContent();
                        if(!typeLookup.containsKey(containerId))
                        {
                            errors.add(currentNode.getLineNumber()+": Type "+containerId+" is not a declared type");

                        }
                        else
                        {
                            DeclaredType arrayType = new DeclaredType(dimension, typeLookup.get(containerId), "");
                            if(!addType(alias, arrayType))
                            {
                                errors.add(currentNode.getLineNumber()+": Type "+alias+" has already been declared");

                            }
                        }

                    }
                    else
                    {
                        String id = ((Terminal)typeNode.getChildren().get(0).getChildren().get(0).getChildren().get(0).getData()).getContent();
                        if(!typeLookup.containsKey(id))
                        {
                            errors.add(currentNode.getLineNumber()+": Type "+id+" is not a declared type");

                        }
                        else
                        {
                            DeclaredType oldType = typeLookup.get(id);
                            DeclaredType newType = new DeclaredType(alias, "");
                            if(!addType(alias, newType))
                            {
                                errors.add(currentNode.getLineNumber()+": Type "+alias+" has already been declared");

                            }
                            else
                                if(!addTypeMap(newType, oldType))
                                {
                                    errors.add(currentNode.getLineNumber()+": Type "+alias+" has already been declared");

                                }

                        }

                    }

                }
            }
        }
        else
            throw new IllegalArgumentException();

        return errors;

    }

    private List<String> populateVars(Node<Type> subRoot)
    {
        if(subRoot.getData()==RuleType.VAR_DECLARATION_LIST)
        {
            //subRoot is a var_decl-list
            List<String> errors = new ArrayList<>();
            for(Node<Type> currentNode:subRoot)
            {
                if(currentNode.getData()==RuleType.VAR_DECLARATION)
                {
                    //new variable declaration
                    Node<Type> id_list = currentNode.getChildren().get(1);
                    Node<Type> type_id = currentNode.getChildren().get(3);
                    String typeid = ((Terminal)type_id.getChildren().get(0).getChildren().get(0).getData()).getContent();   //type-id->id->terminal
                    DeclaredType type = findType(typeid);
                    if(type!=null)
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

                           String error = addVariable(id, type, "");
                            if(!error.equals(""))
                            {
                                errors.add(currentNode.getLineNumber()+": "+error);
                            }

                        }
                    }
                    else
                    {
                        errors.add(currentNode.getLineNumber()+": Type "+typeid+" has not been declared");

                    }

                }
            }
            return errors;
        }
        else
            throw new IllegalArgumentException();

    }
    private String addVariable(String id, DeclaredType type, String scope)
    {
        Binding entry = new Binding(id,type,scope);
        if(identifiers.containsKey(id))
        {
            if(identifiers.get(id).contains(entry))
            {
                return id+" has already been declared in this context";
            }
            else
            {
                identifiers.get(id).add(entry);
            }
        }
        else
        {
            List<Binding> entries = new ArrayList<Binding>();
            entries.add(entry);
            identifiers.put(id, entries);
        }
        return "";
    }
    private String addFunction(String id, DeclaredType returnType, List<DeclaredType> params)
    {
        Binding entry = new Binding(id,returnType,"", params);
        if(identifiers.containsKey(id))
        {
            if(identifiers.get(id).contains(entry))
            {
                //error, var of this scope and name exists in the table already
                return id+" has already been declared in this context";
            }
            else
            {
                identifiers.get(id).add(entry);
            }
        }
        else
        {
            List<Binding> entries = new ArrayList<Binding>();
            entries.add(entry);
            identifiers.put(id, entries);
        }
        return "";
    }
    private List<String> populateFunctions(Node<Type> subRoot)
    {
        if(subRoot.getData()== RuleType.FUNCT_DECLARATION_LIST)
        {
            List<String> errors = new ArrayList<>();
            for(Node<Type> currentNode: subRoot)
            {
                if(currentNode.getData()==RuleType.FUNCT_DECLARATION)
                {
                    List<DeclaredType> parameters = new ArrayList<DeclaredType>();
                    String funcId = ((Terminal)currentNode.getChildren().get(1).getChildren().get(0).getData()).getContent();
                    Node<Type> param_list = currentNode.getChildren().get(3);
                    Node<Type> return_type = currentNode.getChildren().get(5);
                    if(!param_list.isEpsilon())
                    {
                        for(Node<Type> param: param_list)
                        {
                            if(param.getData()==RuleType.PARAM)
                            {
                                String paramId = ((Terminal)param.getChildren().get(0).getChildren().get(0).getData()).getContent();
                                String paramTypeId = ((Terminal)param.getChildren().get(2).getChildren().get(0).getChildren().get(0).getData()).getContent();
                                DeclaredType paramType = findType(paramTypeId);
                                if(paramType!=null)
                                {
                                    addVariable(paramId, paramType, funcId);
                                    parameters.add(paramType);
                                }
                                else
                                {
                                    errors.add(currentNode.getLineNumber()+": Type "+paramTypeId+" has not been declared");
                                }



                            }
                        }
                    }
                    //grab return type
                    DeclaredType returnType = null;
                    if(!return_type.isEpsilon())
                    {
                        String returnId = ((Terminal)return_type.getChildren().get(1).getChildren().get(0).getChildren().get(0).getData()).getContent();
                        returnType = findType(returnId);
                        if(returnType==null)
                        {
                            errors.add(currentNode.getLineNumber()+": Type "+returnId+" has not been declared");
                            continue;
                        }

                    }
                   String error =  addFunction(funcId, returnType, parameters);
                    if(!error.equals(""))
                    {
                        errors.add(currentNode.getLineNumber()+": "+error);
                    }



                }
            }
            return errors;
        }
        else
            throw new IllegalArgumentException();
    }
    private boolean addTypeMap(DeclaredType alias, DeclaredType aliased)
    {
        if(typeMapping.containsKey(alias))
            return false;
        typeMapping.put(alias, aliased);
        return true;
    }
    private boolean addType(String id, DeclaredType type)
    {
        if(typeLookup.containsKey(id))
            return false;
        typeLookup.put(id, type);
        return true;
    }


    @Override
    public List<Binding> find(String name)
    {
        return identifiers.get(name);
    }

    @Override
    public Binding findByNameScope(String name, String scope)
    {
        List<Binding> entries = find(name);
        if(entries == null)
            return null;
        if(entries.size()==1)
        {
            if(entries.get(0).getScope().equals(""))
                return entries.get(0);
        }
        for(Binding b: entries)
        {
            if(b.getScope().equals(scope))
            {
                return b;
            }
        }
        return null;
    }

    public DeclaredType findType(String name)
    {
        return typeLookup.get(name);
    }
    //TODO: does not work
    public DeclaredType findPrimitive(String name,String scope)
    {
        Binding b = findByNameScope(name, scope);
        if(b==null)
            return null;
        DeclaredType type = b.getType();
        while(true)
        {
            if(type.equals(DeclaredType.integer)||type.equals(DeclaredType.str)||type.isArray())
            {
                return type;
            }
            type = typeMapping.get(type);
        }


    }
    public DeclaredType findTypeMap(DeclaredType type)
    {
        if(type!=null&&(type.equals(DeclaredType.integer)||type.equals(DeclaredType.str)||type.isArray()))
        {
            return type;
        }
        else
        {
            while(true)
            {

                DeclaredType map = typeMapping.get(type);
                if(map==null)
                    return null;
                else  if(map.equals(DeclaredType.integer)||map.equals(DeclaredType.str)||map.isArray())
                {
                    return map;
                }
            }
        }
    }
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("TYPES\n---------\n");
        for(Map.Entry<String, DeclaredType> e:  typeLookup.entrySet ())
        {
            sb.append(e.getKey());
            sb.append(": ");
            sb.append(e.getValue()+"\n");
        }
        sb.append("VARIABLES\n---------\n");
        for(Map.Entry<String, List<Binding> >e: identifiers.entrySet())
        {
            for(Binding b:e.getValue())
            {
                if(!b.isFunction())
                {
                    sb.append(e.getKey());
                    sb.append(": ");
                    sb.append(b+"\n");
                }

            }
        }
        sb.append("FUNCTIONS\n---------\n");
        for(Map.Entry<String, List<Binding> >e: identifiers.entrySet())
        {
            for(Binding b:e.getValue())
            {
                if(b.isFunction())
                {
                    sb.append(e.getKey());
                    sb.append(": ");
                    sb.append(b+"\n");
                }

            }
        }
        return sb.toString();

    }

}
