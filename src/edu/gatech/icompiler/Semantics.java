package edu.gatech.icompiler;

import edu.gatech.util.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 3/10/14.
 */
public class Semantics
{
    private final Node<Type> tree;
    private final List<Node<Type>> statements;

    public Semantics(Node<Type> tree)
    {
        this.tree = tree;
        statements = new ArrayList<>();
        getStatements();
    }

    private void getStatements()
    {
        for(Node<Type> node: tree )
        {
            if(node.getData().isToken() && ((RuleType)node.getData())==RuleType.STAT)
            {
                statements.add(node);
            }
        }
    }

    public boolean performChecks()
    {
        for(Node<Type> statement: statements)
        {
            //checks here
        }
        return false;
    }
}
