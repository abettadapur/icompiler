package edu.gatech.util;

/**
 * Created by Alex on 2/25/14.
 */
public class Tree<T>
{
    Node<T> root;

    public Tree()
    {
        this(null);
    }
    public Tree(Node<T> root)
    {
        this.root=root;
    }

}
