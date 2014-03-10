package edu.gatech.util;

import edu.gatech.icompiler.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Alex on 2/25/14.
 */
public class Node<T> implements Iterable<Node<T>>
{
    private List<Node<T>> children;
    private Node<T> parent;
    private int currentChild;
    private T data;
    private static int tabLevel=0;

    public Node(T data)
    {
        this.data=data;
        this.currentChild=-1;
        children = new ArrayList<>();
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

    public T getData(){
        return data;
    }

    public boolean isLeaf()
    {
        return children.size()==0;
    }

    public void printPreOrder()
    {
        System.out.println(preOrder(this));
        tabLevel=0;
    }
    private String preOrder(Node<T> node)
    {
       StringBuilder out = new StringBuilder();
       if(node!=null)
       {
           for(int i=0; i<tabLevel; i++)
               out.append('\t');
           if(!node.isLeaf())
           {
            out.append("<"+node.data+">\n");
            tabLevel++;

            for(int i=0; i<node.children.size(); i++)
            {
                out.append(preOrder(node.children.get(i)));
            }

           tabLevel--;
           for(int i=0; i<tabLevel; i++)
               out.append('\t');
           out.append("</" + node.data + ">\n");
           }
           else
               out.append("<" + node.data + "/>\n");


       }
        return out.toString();
    }

    @Override
    public Iterator<Node<T>> iterator()
    {
        return new PreOrderIterator<>(this);
    }
}
