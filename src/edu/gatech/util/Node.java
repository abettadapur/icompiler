package edu.gatech.util;

import edu.gatech.icompiler.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 2/25/14.
 */
public class Node<T>
{
    private List<Node<T>> children;
    private Node<T> parent;
    private int currentChild;
    private T data;

    public Node(T data)
    {
        this.data=data;
        this.currentChild=-1;
        children = new ArrayList<>();
    }

    public void setParent(Node<T> parent)
    {
        this.parent=parent;
    }

    public Node<T> getParent() {
        return parent;
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
            if(currentChild==-1)
                currentChild=0;
        }
    }
    public Node<T> getNextChild()
    {
        if(currentChild==children.size())
        {
            if(parent!=null)
                return parent.getNextChild();
            else
                return null;
        }
        else
        {
            return children.get(currentChild++);
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

    public void printPreOrder()
    {
        preOrder(this);
    }
    private void preOrder(Node<T> node)
    {
       if(node!=null)
       {
           System.out.println(node.data);
           for(int i=0; i<node.children.size(); i++)
           {
               preOrder(node.children.get(i));
           }
       }

    }
}
