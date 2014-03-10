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

    }
    public boolean performChecks()
    {
        return false;
    }
}
