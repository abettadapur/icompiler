package edu.gatech.icompiler;

import edu.gatech.facade.ITable;
import edu.gatech.util.Node;
import org.junit.Rule;

import javax.lang.model.type.DeclaredType;
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
            if(node.getData()==RuleType.FUNCT_DECLARATION)
            {
                for(Node<Type> child:node.getChildren())
                {
                    if(child.getData()==TokenType.ID)
                    {
                        scope = child.getChildren().get(0).getData().toString().replace("\"","");
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
                PrimitiveType typeId = PrimitiveType.unknown;
                PrimitiveType constType = PrimitiveType.unknown;
                for(int i=0; i<declaration.getChildren().size(); i++)
                {
                    Node<Type> child = declaration.getChildren().get(i);
                    if(child.getData()==RuleType.TYPE_ID)
                    {
                        typeId= getPrimitiveFromId(child.getChildren().get(0).toString().replace("\"", ""),"");
                    }

                    if(child.getData()==RuleType.OPTIONAL_INIT)
                    {
                        Node<Type> constant = child.getChildren().get(1);
                        constType = getConstType(constant);
                    }
                }
                //compare types for initialization
                if(!(typeId==PrimitiveType.unknown))
                    if(typeId!=constType)
                        errors.add(declaration.getLineNumber()+": "+typeId.name()+" does not match "+constType.name());
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

    public PrimitiveType evaluateExpression(Node<Type> subRoot, boolean seenComparator)
    {
        PrimitiveType currType = PrimitiveType.unknown;
        PrimitiveType compareType = PrimitiveType.unknown;
        TokenType currentOperator = null;
        if(subRoot.getData()==RuleType.EXPR)
        {
            if(subRoot.getChildren().get(0).getData()==TokenType.LPAREN)
            {
                PrimitiveType exprType = evaluateExpression(subRoot.getChildren().get(1), seenComparator);
                Node<Type> opexpr = subRoot.getChildren().get(3);
                if(!opexpr.isEpsilon())
                {
                    TokenType operator = (TokenType)opexpr.getChildren().get(0).getData();
                    boolean comparator = operator==TokenType.EQ||operator==TokenType.NEQ||operator==TokenType.GEQ||operator==TokenType.LEQ||operator==TokenType.GREATER||operator==TokenType.LESSER;
                    Node<Type> expression = opexpr.getChildren().get(1);
                    PrimitiveType newType = evaluateExpression(expression,comparator);
                    if( typeCompatibility(exprType, operator, newType));
                    {
                        if(comparator)
                            return PrimitiveType.integer;
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
                        PrimitiveType newType = evaluateLValue(current);
                        if(newType==PrimitiveType.unknown)
                        {
                            currType = PrimitiveType.unknown; //error with the lvalue, should have been logged
                            break;
                        }
                        //this is the first type we have seen
                        if(currType==PrimitiveType.unknown)
                        {
                            currType = newType;
                        }
                        //we have an operator, must see if types are compatible
                        else if(currentOperator!=null)
                        {

                            if(!typeCompatibility(currType, currentOperator, newType))
                            {
                                errors.add(current.getLineNumber()+": "+currentOperator.name()+" does not support arguments "+currType.name() +" and "+  newType.name() );
                                currType = PrimitiveType.unknown;
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
                            currType = PrimitiveType.unknown;
                            break;
                        }

                    }
                    if(current.getData()==RuleType.CONST)
                    {
                        PrimitiveType newType = getConstType(current);
                        if(currType==PrimitiveType.unknown)
                            currType = newType;
                        else if(currentOperator!=null)
                        {
                            if(!typeCompatibility(currType, currentOperator, newType))
                            {
                                errors.add(current.getLineNumber()+": "+currentOperator.name()+" does not support arguments "+currType.name() +" and "+  newType.name() );
                                currType = PrimitiveType.unknown;
                                break;
                            }
                        }
                        else
                        {
                            errors.add("Unknown parse tree error. Missing operator");
                            currType = PrimitiveType.unknown;
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
                                currType=PrimitiveType.unknown;
                                currentOperator=null;
                            }
                            else
                            {
                                //ERRORS
                                currType = PrimitiveType.unknown;
                                break;
                            }
                        }
                    }
                }
            }



            if(seenComparator)
            {
                if(compareType==currType)
                    currType=PrimitiveType.integer;
                else
                {
                    //error, compare two different ops;
                    currType=PrimitiveType.unknown;
                }
            }
            return currType;
        }
        else
            throw new IllegalArgumentException();
    }

    public boolean typeCompatibility(PrimitiveType type1, TokenType operator, PrimitiveType type2)
    {
        if(type1!=type2)
            return false;
        if(type1==PrimitiveType.str)
        {
            if(operator!=TokenType.EQ && operator!=TokenType.NEQ&&operator!=TokenType.ASSIGN)
                return false;
        }
        if(type1== PrimitiveType.arr)
        {
            if(operator!=TokenType.ASSIGN)
                return false;
        }
        if(type1==PrimitiveType.integer)
        {
            //all possible operators
            if(operator!=TokenType.EQ&&operator!=TokenType.GEQ&&operator!=TokenType.ASSIGN&&operator!=TokenType.NEQ&&operator!=TokenType.LEQ&&operator!=TokenType.MINUS&&operator!=TokenType.MULT&&operator!=TokenType.PLUS&&operator!=TokenType.DIV&&operator!=TokenType.GREATER&&operator!=TokenType.LESSER)
            {
                return false;
            }
        }
        return true;
    }

    public PrimitiveType evaluateLValue(Node<Type> subRoot)
    {
        int dimension=0;
        PrimitiveType curType = PrimitiveType.unknown;
        for(Node<Type> sel:subRoot)
        {
            if(sel.getData()==TokenType.ID)
            {
                curType = getPrimitiveFromId(sel.getChildren().get(0).toString().replace("\"",""), sel.getScope());
                if(curType==PrimitiveType.unknown)
                {
                    //problem with ID. should have been logged already
                    break;
                }
            }
            if(sel.getData()==RuleType.EXPR)
            {
                dimension++;
                if(!(curType==PrimitiveType.arr))
                {
                    errors.add(sel.getLineNumber()+": "+curType.name()+" does not support []");
                    curType = PrimitiveType.unknown;
                    break;
                }
                //TODO: getArrayDimensions....
                if(evaluateExpression(sel,false)!=PrimitiveType.integer)
                {
                    errors.add(sel.getLineNumber()+": Expression contained [] evaluated to unsupported type");
                    curType = PrimitiveType.unknown;
                    break;
                }
            }
        }
        return curType;
    }

    public PrimitiveType getPrimitiveFromId(String id, String scope)
    {
        //perform lookups
        return PrimitiveType.integer;
    }

    public boolean evaluateFor(Node<Type> subRoot)
    {
        if(subRoot.hasChildOfType(TokenType.FOR))
        {
            Node<Type> id = subRoot.getChildren().get(1);
            Node<Type> expression1 = subRoot.getChildren().get(3);
            Node<Type> expression2 = subRoot.getChildren().get(5);
            PrimitiveType errorType;

            if(getPrimitiveFromId(id.getChildren().get(0).getData().toString().replace("\"",""), id.getScope())!=PrimitiveType.integer)
            {
                errors.add(id.getLineNumber()+": For loops only support integer types");
                return false;
            }
            if((errorType = evaluateExpression(expression1,false))!=PrimitiveType.integer)
            {
                errors.add(id.getLineNumber()+": Expected int, expression evaluated to "+errorType.name());
                return false;
            }
            if((errorType= evaluateExpression(expression2,false))!=PrimitiveType.integer)
            {
                errors.add(id.getLineNumber()+": Expected int, expression evaluated to "+errorType.name());
                return false;
            }
            return true;
        }
        else
            throw new IllegalArgumentException();
    }
    public boolean evaluateWhile(Node<Type> subRoot)
    {
        if(subRoot.hasChildOfType(TokenType.WHILE))
        {
            Node<Type> expression = subRoot.getChildren().get(1);
            if(evaluateExpression(expression,false)!=PrimitiveType.integer)
            {
               errors.add(subRoot.getLineNumber()+": expression does not evaluate to true or false");
               return false;
            }
            return true;
        }
        else
            throw new IllegalArgumentException();

    }
    public boolean evaluateIf(Node<Type> subRoot)
    {
        if(subRoot.hasChildOfType(TokenType.IF))
        {
            Node<Type> expression = subRoot.getChildren().get(1);
            if(evaluateExpression(expression,false)!=PrimitiveType.integer)
            {
                errors.add(subRoot.getLineNumber()+": expression does not evaluate to true or false");
                return false;
            }
            return true;
        }
        else
            throw new IllegalArgumentException();
    }
    public boolean evaluateReturn(Node<Type> subRoot)
    {
        if(subRoot.hasChildOfType(TokenType.RETURN))
        {
            String functionId = subRoot.getScope();
            //TODO: lookup symbol table;

        }
        return false;
    }
    public boolean evaluateStatAssign(Node<Type> subRoot)
    {
        if(subRoot.hasChildOfType(RuleType.STAT_ASSIGN))
        {

        }
        return false;
    }

    public PrimitiveType getConstType(Node<Type> constant)
    {
        if(constant.getData()==RuleType.CONST)
            if(constant.getChildren().get(0).getData()==TokenType.INTLIT)
                return PrimitiveType.integer;
            else
                return PrimitiveType.str;
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
