package edu.gatech.icompiler;

import edu.gatech.facade.ITable;
import edu.gatech.util.Node;


import javax.lang.model.type.PrimitiveType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 3/10/14.
 */
public class Semantics
{
    private final List<String> errors;
    private final Node<Type> tree;
    private final List<Node<Type>> declarations;
    private final List<Node<Type>> statements;
    private final ITable symbolTable;

    public Semantics(Node<Type> tree, ITable symbolTable)
    {
        this.tree = tree;
        this.symbolTable = symbolTable;
        declarations = new ArrayList<>();
        statements = new ArrayList<>();
        errors = new ArrayList<>();
        annotateTree();
        getStatements();

    }

    private void annotateTree()
    {
        String scope="";
        for(Node<Type> node: tree)
        {
            if(node.getData()== RuleType.FUNCT_DECLARATION)
            {
                for(Node<Type> child:node.getChildren())
                {
                    if(child.getData()== TokenType.ID)
                    {
                        scope = ((Terminal)child.getChildren().get(0).getData()).getContent();
                        break;
                    }
                }
            }
            if(node.getData()==TokenType.ID||node.getData()==RuleType.STAT)
            {
                node.setScope(scope);
            }
            if(node.getData()==TokenType.END)
                scope="";
        }
    }

    private void getStatements()
    {
        for(Node<Type> node: tree )
        {
            if(node.getData()==RuleType.STAT)
            {
                statements.add(node);
            }
            if(node.getData()==RuleType.VAR_DECLARATION)
            {
                declarations.add(node);
            }
        }

    }

    public List<String> performChecks()
    {
        //check optionally initialized types
        for(Node<Type> declaration: declarations)
        {
            if(declaration.hasChildOfType(RuleType.OPTIONAL_INIT))
            {
                DeclaredType typeId = null;
                DeclaredType constType = null;
                for(int i=0; i<declaration.getChildren().size(); i++)
                {
                    Node<Type> child = declaration.getChildren().get(i);
                    if(child.getData()==RuleType.TYPE_ID)
                    {
                        typeId= getPrimitiveFromId(((Terminal)child.getChildren().get(0).getChildren().get(0).getData()).getContent(), "");
                    }

                    if(child.getData()==RuleType.OPTIONAL_INIT)
                    {
                        Node<Type> constant = child.getChildren().get(1);
                        constType = getConstType(constant);
                    }
                }
                //compare types for initialization
                if(!(typeId==null))
                    if(!typeId.equals(constType))
                        errors.add(declaration.getLineNumber()+": "+typeId.getTypeName()+" does not match "+constType.getTypeName());
            }
        }
        //TODO: MUST SEARCH FOR RETURN STATEMENTS
        for(Node<Type> statement: statements)
        {
            if(statement.getLineNumber()==18)
            {
                int a=0;
            }
            if(statement.hasChildOfType(TokenType.FOR))
            {
                evaluateFor(statement);
            }
            else if (statement.hasChildOfType(TokenType.IF))
            {
                evaluateIf(statement);
            }

            else if (statement.hasChildOfType(TokenType.WHILE))
            {
                evaluateWhile(statement);
            }
            else if(statement.hasChildOfType(TokenType.RETURN))
            {
                evaluateReturn(statement);
            }
            else if (statement.hasChildOfType(RuleType.STAT_ASSIGN))
            {
                evaluateStatAssign(statement);
            }
        }
        return errors;
    }

    public DeclaredType evaluateExpression(Node<Type> subRoot, boolean seenComparator)
    {
        DeclaredType currType = null;
        DeclaredType compareType = null;
        TokenType currentOperator = null;
        if(subRoot.getData()==RuleType.EXPR)
        {
            if(subRoot.getChildren().get(0).getData()==TokenType.LPAREN)
            {
                DeclaredType exprType = evaluateExpression(subRoot.getChildren().get(1), seenComparator);
                if(exprType==null)
                {
                    return null;
                }
                Node<Type> opexpr = subRoot.getChildren().get(3);
                if(!opexpr.isEpsilon())
                {
                    TokenType operator = (TokenType)opexpr.getChildren().get(0).getData();
                    boolean comparator = operator==TokenType.EQ||operator==TokenType.NEQ||operator==TokenType.GEQ||operator==TokenType.LEQ||operator==TokenType.GREATER||operator==TokenType.LESSER;
                    Node<Type> expression = opexpr.getChildren().get(1);
                    DeclaredType newType = evaluateExpression(expression,comparator);
                    if( typeCompatibility(exprType, operator, newType));
                    {
                        if(comparator)
                            return DeclaredType.integer;
                        else
                            return exprType;
                    }

                }
                else
                    return exprType;

            }
            else if(subRoot.getChildren().get(0).getData()==TokenType.MINUS)
            {
                return evaluateExpression(subRoot.getChildren().get(1),false);
            }
            else
            {
                for(Node<Type> current: subRoot)
                {
                    if(current.getData()==RuleType.LVALUE)
                    {
                        DeclaredType newType = evaluateLValue(current);
                        if(newType==null)
                        {
                            currType = null; //error with the lvalue, should have been logged
                            break;
                        }
                        //this is the first type we have seen
                        if(currType==null)
                        {
                            currType = newType;
                        }
                        //we have an operator, must see if types are compatible
                        else if(currentOperator!=null)
                        {

                            if(!typeCompatibility(currType, currentOperator, newType))
                            {
                                errors.add(current.getLineNumber()+": "+currentOperator.name()+" does not support arguments "+currType.getTypeName() +" and "+  newType.getTypeName() );
                                currType = null;
                                break;
                            }
                            else
                            {
                                //check passed, reset operator
                                currentOperator=null;
                            }
                        }
                        else
                        {
                            errors.add("Unknown parse tree error. Missing operator");
                            currType = null;
                            break;
                        }

                    }
                    if(current.getData()==RuleType.CONST)
                    {
                        DeclaredType newType = getConstType(current);
                        if(currType==null)
                            currType = newType;
                        else if(currentOperator!=null)
                        {
                            if(!typeCompatibility(currType, currentOperator, newType))
                            {
                                errors.add(current.getLineNumber()+": "+currentOperator.name()+" does not support arguments "+currType.getTypeName() +" and "+  newType.getTypeName() );
                                currType = null;
                                break;
                            }
                        }
                        else
                        {
                            errors.add("Unknown parse tree error. Missing operator");
                            currType = null;
                            break;
                        }
                    }
                    //here is an operator
                    if(current.getData()==RuleType.MULTOP||current.getData()==RuleType.ADDOP||current.getData()==RuleType.BINOP)
                    {
                        //grab that operator
                        currentOperator = (TokenType)current.getChildren().get(0).getData();
                        if(currentOperator==TokenType.EQ||currentOperator==TokenType.NEQ||currentOperator==TokenType.GEQ||currentOperator==TokenType.LEQ||currentOperator==TokenType.GREATER||currentOperator==TokenType.LESSER)
                        {
                            if(!seenComparator)
                            {
                                seenComparator=true;
                                compareType = currType;
                                currType=null;
                                currentOperator=null;
                            }
                            else
                            {
                                //ERRORS
                                currType = null;
                                break;
                            }
                        }
                    }
                }
            }



            if(seenComparator)
            {

                if(compareType==null)
                    return currType;
                if(compareType==currType)
                    currType=DeclaredType.integer;
                else
                {
                    errors.add(subRoot.getLineNumber()+": Cannot compare "+compareType.getTypeName()+" to "+currType.getTypeName());
                    currType=null;
                }
            }
            return currType;
        }
        else
            throw new IllegalArgumentException();
    }

    public boolean typeCompatibility(DeclaredType type1, TokenType operator, DeclaredType type2)
    {
        DeclaredType type1prim = getPrimitiveFromType(type1);
        DeclaredType type2prim = getPrimitiveFromType(type2);
        if(type1prim!=null&&!type1prim.equals(type2prim))
        {
            return false;
        }
        if(type1prim.equals(DeclaredType.str))
        {
            if(operator!=TokenType.EQ && operator!=TokenType.NEQ&&operator!=TokenType.ASSIGN)
                return false;
        }
        if(type1prim.isArray())
        {
            if(operator!=TokenType.ASSIGN)
                return false;
        }
        if(type1prim.equals(DeclaredType.integer))
        {
            //all possible operators
            if(operator!=TokenType.EQ&&operator!=TokenType.GEQ&&operator!=TokenType.ASSIGN&&operator!=TokenType.NEQ&&operator!=TokenType.LEQ&&operator!=TokenType.MINUS&&operator!=TokenType.MULT&&operator!=TokenType.PLUS&&operator!=TokenType.DIV&&operator!=TokenType.GREATER&&operator!=TokenType.LESSER)
            {
                return false;
            }
        }
        return true;
    }

    private DeclaredType getPrimitiveFromType(DeclaredType type2)
    {
        return symbolTable.findTypeMap(type2);
    }

    //TODO: getArrayTypes
    public DeclaredType evaluateLValue(Node<Type> subRoot)
    {
        int dimension=0;
        DeclaredType curType = null;
        for(Node<Type> sel:subRoot)
        {
            if(sel.getData()==TokenType.ID)
            {
                String id = ((Terminal)sel.getChildren().get(0).getData()).getContent();
                curType = getPrimitiveFromId(id, sel.getScope());
                if(curType==null)
                {
                    errors.add(subRoot.getLineNumber()+": "+id+" could not be found in the current context");
                    return null;
                }
            }
            if(sel.getData()==RuleType.EXPR)
            {
                //we are dealing with array
                dimension++;
                if(!(curType.isArray()))
                {
                    errors.add(sel.getLineNumber()+": "+curType.getTypeName()+" does not support []");
                    return null;
                }
                //TODO: getArrayDimensions....
                if(!evaluateExpression(sel,false).equals(DeclaredType.integer))
                {
                    errors.add(sel.getLineNumber()+": Expression contained [] evaluated to unsupported type");
                    return null;
                }
            }
        }
        if(dimension>0)
        {
            if(curType.isArray()&&curType.getDimensionCount()==dimension)
                return curType.getContainer();
            else
            {
                errors.add(subRoot.getLineNumber()+": Array dimension mismatch");
                return null;
            }
        }
        return curType;
    }

    public DeclaredType getPrimitiveFromId(String id, String scope)
    {
        return symbolTable.findPrimitive(id,scope);
    }

    public void evaluateFor(Node<Type> subRoot)
    {
        if(subRoot.hasChildOfType(TokenType.FOR))
        {
            Node<Type> id = subRoot.getChildren().get(1);
            Node<Type> expression1 = subRoot.getChildren().get(3);
            DeclaredType expression1Type = evaluateExpression(expression1, false);
            Node<Type> expression2 = subRoot.getChildren().get(5);
            DeclaredType expression2Type = evaluateExpression(expression2, false);
            DeclaredType errorType;

            if(!getPrimitiveFromId(((Terminal)id.getChildren().get(0).getData()).getContent(), id.getScope()).equals(DeclaredType.integer))
            {
                errors.add(id.getLineNumber()+": For loops only support integer types");

            }

            if(expression1Type!=null&&!expression1Type.equals(DeclaredType.integer))
            {
                errors.add(id.getLineNumber() + ": Expected int, expression evaluated to " + expression1Type.getTypeName());

            }
            if(expression2Type!=null&&!expression2Type.equals(DeclaredType.integer))
            {
                errors.add(id.getLineNumber()+": Expected int, expression evaluated to "+expression2Type.getTypeName());

            }

        }
        else
            throw new IllegalArgumentException();
    }
    public void evaluateWhile(Node<Type> subRoot)
    {
        if(subRoot.hasChildOfType(TokenType.WHILE))
        {
            Node<Type> expression = subRoot.getChildren().get(1);
            DeclaredType expressionType = evaluateExpression(expression,false);
            if(expressionType!=null&&!expressionType.equals(DeclaredType.integer))
            {
                errors.add(subRoot.getLineNumber()+": expression does not evaluate to true or false");

            }

        }
        else
            throw new IllegalArgumentException();

    }
    public void evaluateIf(Node<Type> subRoot)
    {
        if(subRoot.hasChildOfType(TokenType.IF))
        {
            Node<Type> expression = subRoot.getChildren().get(1);
            DeclaredType expressionType = evaluateExpression(expression, false);
            if(expressionType!=null&&!expressionType.equals(DeclaredType.integer))
            {
                errors.add(subRoot.getLineNumber()+": expression does not evaluate to true or false");

            }

        }
        else
            throw new IllegalArgumentException();
    }
    public void evaluateReturn(Node<Type> subRoot)
    {
        if(subRoot.hasChildOfType(TokenType.RETURN))
        {
            String functionId = subRoot.getScope();
            DeclaredType expressionType = evaluateExpression(subRoot.getChildren().get(1), false);
            DeclaredType returnType = symbolTable.findPrimitive(functionId, "");
            if(expressionType!=null&&!expressionType.equals(returnType))
            {
                errors.add(subRoot.getLineNumber()+": "+expressionType.getTypeName()+" does not match expected return type "+returnType.getTypeName());

            }

        }

    }
    public void evaluateStatAssign(Node<Type> subRoot)
    {
        if(subRoot.hasChildOfType(RuleType.STAT_ASSIGN))
        {
            Node<Type> identifier = subRoot.getChildren().get(0);

            Node<Type> statAssign = subRoot.getChildren().get(1);

            if(statAssign.getChildren().get(0).getData()==TokenType.LPAREN)
            {
                //TODO: check existence of function. If exist, ok. Must Also check parameters
                String id = ((Terminal)identifier.getChildren().get(0).getData()).getContent();
                Binding binding = symbolTable.findByNameScope(id,"");
                if(binding == null)
                {
                    errors.add(subRoot.getLineNumber()+": Cannot find symbol "+id+" in current context");
                }
                else
                {
                    if(!binding.isFunction())
                    {
                        errors.add(subRoot.getLineNumber()+": "+id+" is not a function");
                    }
                    else
                    {
                        //TODO: parameter checking
                        List<DeclaredType> expParameters = binding.getParams();
                        List<DeclaredType> actParameters = new ArrayList<DeclaredType>();
                        Node<Type> expressionList = statAssign.getChildren().get(1);
                        for(Node<Type> currentNode: expressionList)
                        {
                            if(currentNode.getData()==RuleType.EXPR)
                                actParameters.add(evaluateExpression(currentNode, false));
                        }
                        if(expParameters.size()!=actParameters.size())
                        {
                            errors.add(subRoot.getLineNumber()+": "+id+" takes parameters "+expParameters+" found parameters"+actParameters);
                        }
                        else
                        {
                            for(int i=0; i<expParameters.size(); i++)
                            {
                                if(!typeCompatibility(expParameters.get(i), TokenType.ASSIGN, actParameters.get(i)))
                                    errors.add(subRoot.getLineNumber()+": "+id+" takes parameters "+expParameters+" found parameters"+actParameters);
                            }
                        }

                    }
                }
            }

            else if(statAssign.getChildren().get(0).getData()==RuleType.LVALUE_TAIL||statAssign.getChildren().get(0).getData()==TokenType.ASSIGN)
            {
                DeclaredType idType;
                DeclaredType secondType = null;
                Node<Type> expr_or_id=null;
                if(statAssign.hasChildOfType(RuleType.LVALUE_TAIL))
                {
                    //make fake subtree, test lvalue
                    Node<Type> lvalueSurrogate = new Node<Type>(RuleType.LVALUE, false, 0);
                    lvalueSurrogate.getChildren().add(identifier);
                    lvalueSurrogate.getChildren().add(statAssign.getChildren().get(0));

                    idType = evaluateLValue(lvalueSurrogate);

                    expr_or_id = statAssign.getChildren().get(2);
                }
                else
                {
                    expr_or_id = statAssign.getChildren().get(2);
                    String idStr = ((Terminal)identifier.getChildren().get(0).getData()).getContent();
                    idType = getPrimitiveFromId(idStr, subRoot.getScope());
                    if(idType == null)
                    {
                        errors.add(subRoot.getLineNumber()+": Could not find "+idStr+" in current context");
                        return;
                    }
                }

                if(expr_or_id.getChildren().get(0).getData()==RuleType.EXPR_NO_LVALUE)
                {
                    Node<Type> expr_no_lvalue = expr_or_id.getChildren().get(0);
                    if(expr_no_lvalue.getChildren().get(0).getData()==RuleType.CONST)
                    {
                        secondType = getConstType(expr_no_lvalue.getChildren().get(0));
                    }
                    else
                    {
                        //this is now an expression
                        expr_no_lvalue.setData(RuleType.EXPR);
                        secondType = evaluateExpression(expr_no_lvalue, false);
                    }
                }
                else if(expr_or_id.getChildren().get(0).getData()==TokenType.ID)
                {
                    Node<Type> expr_or_funcId = expr_or_id.getChildren().get(0);
                    String expr_or_funcIdStr = ((Terminal)expr_or_funcId.getChildren().get(0).getData()).getContent();
                    Node<Type> expr_or_func = expr_or_id.getChildren().get(1);
                    if(expr_or_func.isEpsilon())
                    {
                        //simple assignment
                        secondType = getPrimitiveFromId(expr_or_funcIdStr,subRoot.getScope());
                    }
                    else if(expr_or_func.getChildren().get(0).getData()==TokenType.LPAREN)
                    {
                        //TODO:function call
                        Binding b = symbolTable.findByNameScope(expr_or_funcIdStr,"");
                        if(b!=null&&b.isFunction())
                        {
                            //TODO: check function parameters
                            List<DeclaredType> expParameters = b.getParams();
                            List<DeclaredType> actParameters = new ArrayList<DeclaredType>();
                            Node<Type> expressionList = expr_or_func.getChildren().get(1);
                            for(Node<Type> currentNode: expressionList)
                            {
                                if(currentNode.getData()==RuleType.EXPR)
                                    actParameters.add(evaluateExpression(currentNode, false));
                            }
                            if(expParameters.size()!=actParameters.size())
                            {
                                errors.add(subRoot.getLineNumber()+": "+expr_or_funcIdStr+" takes parameters "+expParameters+" found parameters"+actParameters);
                            }
                            else
                            {
                                for(int i=0; i<expParameters.size(); i++)
                                {
                                    if(!typeCompatibility(expParameters.get(i), TokenType.ASSIGN, actParameters.get(i)))
                                    {
                                        errors.add(subRoot.getLineNumber()+": "+expr_or_funcIdStr+" takes parameters "+expParameters+" found parameters"+actParameters);
                                        return;
                                    }
                                }
                                secondType = b.getType();
                                if(secondType == null)
                                {
                                    errors.add(subRoot.getLineNumber()+": "+expr_or_funcIdStr+" doesn't return anything");
                                }

                            }

                        }
                        else
                        {
                            errors.add(subRoot.getLineNumber()+": "+expr_or_funcIdStr+" is not a function");
                        }


                    }
                    else
                    {
                        DeclaredType lvalType = null;
                        //TODO: opexpression..........
                        //lvalue_tail opexpr
                        //TODO:make fake lvalue again, figure out how to use opexpr without rewriting eval expr

                            Node<Type> lvalueSurrogate = new Node<Type>(RuleType.LVALUE, false, 0);
                            lvalueSurrogate.getChildren().add(expr_or_funcId);
                            lvalueSurrogate.getChildren().add(expr_or_func.getChildren().get(0));
                            lvalType = evaluateLValue(lvalueSurrogate);

                        if(lvalType!=null)
                        {
                            TokenType operator = (TokenType)expr_or_func.getChildren().get(1).getChildren().get(0).getChildren().get(0).getData();
                            boolean seenComparator = operator==TokenType.EQ||operator==TokenType.NEQ||operator==TokenType.GEQ||operator==TokenType.LEQ||operator==TokenType.GREATER||operator==TokenType.LESSER;
                            Node<Type> expression = expr_or_func.getChildren().get(1).getChildren().get(1);
                            DeclaredType exprType = evaluateExpression(expression, seenComparator);
                            if(typeCompatibility(lvalType, operator, exprType))
                            {
                                if(seenComparator)
                                    secondType = DeclaredType.integer;
                                else
                                    secondType = exprType;
                            }
                            else
                            {
                                errors.add(subRoot.getLineNumber()+": "+operator.name()+" does not support types of "+lvalType.getTypeName()+" and "+exprType.getTypeName());

                                return;
                            }
                        }
                        else
                            return;
                    }


                }
                if(idType!=null&&secondType!=null)
                {
                    if(!typeCompatibility(idType, TokenType.ASSIGN, secondType))
                        errors.add(subRoot.getLineNumber()+": Tried to assign "+secondType.getTypeName()+" to "+idType.getTypeName());
                }
            }



        }

    }

    public DeclaredType getConstType(Node<Type> constant)
    {
        if(constant.getData()==RuleType.CONST)
            if(constant.getChildren().get(0).getData()==TokenType.INTLIT)
                return DeclaredType.integer;
            else
                return DeclaredType.str;
        else
            throw new IllegalArgumentException();
    }
    public boolean compareTypes(String type1, String type2)
    {
        return false;
    }
    public boolean compareTypes(String type1, PrimitiveType type2 )
    {
        return false;
    }

}
