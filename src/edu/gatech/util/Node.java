package edu.gatech.util;

import edu.gatech.icompiler.Type;

import java.util.List;

/**
 * Created by Alex on 2/25/14.
 */
public class Node<T>
{
    private List<Node<T>> children;
    private Node<T> parent;
    private T data;

    public Node(T data)
    {
        this.data=data;
    }

    public void setParent(Node<T> parent)
    {
        this.parent=parent;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    public void addChild(Node<T> child)
    {
        if(child!=null&&child.data!=null)
        {
            child.parent=this;
            children.add(child);
        }
    }

    public List<Node<T>> getChildren()
    {
        return children;
    }

    public boolean isLeaf()
    {
        return children.size()==0;
    }
}
