package edu.gatech.util;

import edu.gatech.icompiler.Type;

import java.util.Iterator;
import java.util.Stack;

/**
 * Created by Alex on 3/10/14.
 */
public class PreOrderIterator<T>implements Iterator<Node<T>>{
    Node<T> root;
    Stack<Node<T>> stack;
    public PreOrderIterator(Node<T> root)
    {
        this.root=root;
        this.stack = new Stack<>();
        stack.push(root);
    }


    @Override
    public boolean hasNext()
    {
        return !stack.isEmpty();
    }

    @Override
    public Node<T> next()
    {
        Node<T> ret = stack.pop();

        for(int i=ret.getChildren().size()-1; i>=0; i--)
        {
            stack.push(ret.getChildren().get(i));
        }
        return ret;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
