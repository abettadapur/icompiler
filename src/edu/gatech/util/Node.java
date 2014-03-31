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
    private String scope;
    private int lineNumber;

    public boolean isEpsilon() {
        return isEpsilon;
    }

    private boolean isEpsilon;

    public Node(T data, boolean isEpsilon, int lineNumber)
    {
        this.isEpsilon=isEpsilon;
        this.data=data;
        this.currentChild=-1;
        this.lineNumber=lineNumber;
        children = new ArrayList<>();
        parent=null;
    }

    public int getLineNumber() {
        return lineNumber;
    }
    public void setEpsilon(boolean epsilon)
    {
        this.isEpsilon=epsilon;
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
           if(!node.isEpsilon())
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

       }
        return out.toString();
    }

    public Node<T> getFirstChildOfType(Type target){

        for(Node<T> child : children)
            if(child.getData().equals(target))
                return child;

        return null;
    }

    public boolean hasChildOfType(Type target )
    {
        for(int i=0; i<children.size(); i++)
        {
            Node<T> type = children.get(i);
            if(type.data.equals(target)&&!type.isEpsilon)
                return true;
        }
        return false;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }

    @Override
    public Iterator<Node<T>> iterator()
    {
        return new PreOrderIterator<>(this);
    }

    public Node<T> getNearestParentOfType(Type t)
    {
        Node<T> parent = this.parent;
        while(parent!=null)
        {
            if(parent.getData()==t)
            {
                return parent;
            }
            parent = parent.parent;
        }
        return null;
    }


    public String toString(){
        String childStrings = "\n\tWith immediate children: :::";

        for(Node<T> kid : children)
            childStrings += kid.data + " ::: ";

        return "<" + data.toString() +" in scope " + scope +">" + childStrings ;
    }
}
