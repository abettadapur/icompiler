package edu.gatech.fallback;

import com.sun.javafx.css.Declaration;
import edu.gatech.facade.ITable;
import edu.gatech.fallback.DeclaredType;
import edu.gatech.icompiler.RuleType;
import edu.gatech.icompiler.Terminal;
import edu.gatech.icompiler.TokenType;
import edu.gatech.icompiler.Type;
import edu.gatech.util.Node;
import org.junit.Rule;


import javax.lang.model.type.PrimitiveType;
import javax.print.attribute.standard.PrinterMoreInfoManufacturer;
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
                        typeId= getPrimitiveFromId(((Terminal)child.getChildren().get(0).getData()).getContent(), "");
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
        if(type1.equals(type2))
            return false;
        if(type1.equals(DeclaredType.str))
        {
            if(operator!=TokenType.EQ && operator!=TokenType.NEQ&&operator!=TokenType.ASSIGN)
                return false;
        }
        if(type1.isArray())
        {
            if(operator!=TokenType.ASSIGN)
                return false;
        }
        if(type1.equals(DeclaredType.integer))
        {
            //all possible operators
            if(operator!=TokenType.EQ&&operator!=TokenType.GEQ&&operator!=TokenType.ASSIGN&&operator!=TokenType.NEQ&&operator!=TokenType.LEQ&&operator!=TokenType.MINUS&&operator!=TokenType.MULT&&operator!=TokenType.PLUS&&operator!=TokenType.DIV&&operator!=TokenType.GREATER&&operator!=TokenType.LESSER)
            {
                return false;
            }
        }
        return true;
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
                curType = getPrimitiveFromId(((Terminal)sel.getChildren().get(0).getData()).getContent(), sel.getScope());
                if(curType==null)
                {
                    //error with ID
                    return null;
                }
            }
            if(sel.getData()==RuleType.EXPR)
            {
                //we are dealing with array
                //dimension++;
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
            Node<Type> expression2 = subRoot.getChildren().get(5);
            DeclaredType errorType;

            if(!getPrimitiveFromId(((Terminal)id.getChildren().get(0).getData()).getContent(), id.getScope()).equals(DeclaredType.integer))
            {
                errors.add(id.getLineNumber()+": For loops only support integer types");

            }
            if(!(errorType = evaluateExpression(expression1,false)).equals(DeclaredType.integer))
            {
                errors.add(id.getLineNumber()+": Expected int, expression evaluated to "+errorType.getTypeName());

            }
            if(!(errorType= evaluateExpression(expression2,false)).equals(DeclaredType.integer))
            {
                errors.add(id.getLineNumber()+": Expected int, expression evaluated to "+errorType.getTypeName());

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
            if(!evaluateExpression(expression,false).equals(DeclaredType.integer))
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
            if(!evaluateExpression(expression,false).equals(DeclaredType.integer))
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
            //TODO: lookup symbol table;

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
                //TODO: check existence of function. If exist, ok.
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
                    expr_or_id = statAssign.getChildren().get(1);
                    idType = getPrimitiveFromId(((Terminal)identifier.getChildren().get(0).getData()).getContent(), subRoot.getScope());
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
                    Node<Type> expr_or_func = expr_or_id.getChildren().get(1);
                    if(expr_or_func.getChildren().get(0).getData()==TokenType.LPAREN)
                    {
                        //TODO:function call
                        //check return type
                    }
                    else
                    {
                        //TODO: opexpression..........
                        //lvalue_tail opexpr
                        //TODO:make fake lvalue again, figure out how to use opexpr without rewriting eval expr
                    }


                }
                if(idType!=null&&secondType!=null)
                {
                    if(idType!=secondType)
                        errors.add(subRoot.getLineNumber()+": Tried to assign "+idType.getTypeName()+" to "+secondType.getTypeName());
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