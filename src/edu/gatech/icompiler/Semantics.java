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
        getStatements();
        
    }

    private void getStatements()
    {
        for(Node<Type> node: tree )
        {
            if(node.getData().equals(RuleType.STAT))
            {
                statements.add(node);
            }
            if(node.getData().equals(RuleType.VAR_DECLARATION))
            {
                declarations.add(node);
            }
        }
        
    }

    public boolean performChecks()
    {
        //check optionally initialized types
        for(Node<Type> declaration: declarations)
        {
            if(declaration.hasChildOfType(RuleType.OPTIONAL_INIT))
            {
                String typeId="";
                PrimitiveType constType;
                for(int i=0; i<declaration.getChildren().size(); i++)
                {
                    Node<Type> child = declaration.getChildren().get(i);
                    if(child.getData().equals(RuleType.TYPE_ID))
                    {
                        typeId=child.getChildren().get(0).toString().replace("\"", "");
                    }
                    if(child.getData().equals(RuleType.OPTIONAL_INIT))
                    {
                        Node<Type> constant = child.getChildren().get(1);
                        constType = getConstType(constant);
                    }
                }
                //TODO: Compare Types
            }
        }
        //TODO: MUST SEARCH FOR RETURN STATEMENTS
        for(Node<Type> statement: statements)
        {
            if(statement.hasChildOfType(TokenType.FOR))
            {
                //evaluate for loop
            }
            else if (statement.hasChildOfType(TokenType.IF))
            {
                //evaluate if statement
            }
            else if (statement.hasChildOfType(TokenType.WHILE))
            {
                //evaulate while statement
            }
            else if(statement.hasChildOfType(TokenType.RETURN))
            {
                //evaluate return statement
            }
            else if (statement.hasChildOfType(RuleType.STAT_ASSIGN))
            {
                //evaluate other statements
            }
        }
        return false;
    }

    public PrimitiveType evaluateExpression(Node<Type> subRoot )
    {
        if(subRoot.getData().equals(RuleType.EXPR))
        {
            //operations
            return PrimitiveType.integer;
        }
        else
            throw new IllegalArgumentException();
    }

    public PrimitiveType getConstType(Node<Type> constant)
    {
        if(constant.getData().equals(RuleType.CONST))
            if(constant.getChildren().get(0).equals(TokenType.INTLIT))
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
