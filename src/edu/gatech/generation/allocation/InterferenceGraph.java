package edu.gatech.generation.allocation;

import com.sun.corba.se.impl.orbutil.graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Alex on 4/16/14.
 */
public class InterferenceGraph
{
    List<GraphNode<String>> graph;

    public InterferenceGraph()
    {
        graph = new ArrayList<>();
       /* for(String var: variables)
            graph.add(new GraphNode<String>(var));  //node addition*/


    }

    public void addEdge(String source, String dest)
    {
        GraphNode<String> sourceNode = null;
        GraphNode<String> destNode = null;
        for(GraphNode<String> node: graph)
        {
            if(node.data.equals(source))
                sourceNode = node;
            if(node.data.equals(dest))
                destNode = node;
        }
        if(sourceNode!=null&&destNode!=null)
        {
            if(!sourceNode.neighbors.contains(destNode))
                sourceNode.neighbors.add(destNode);
            if(!destNode.neighbors.contains(destNode))
                destNode.neighbors.add(sourceNode);
        }
    }
    public void addNode(String data)
    {
        if(!graph.contains(new GraphNode<>(data)))
            graph.add(new GraphNode<String>(data));
    }

    public void colorGraph(int K)
    {
        Stack<GraphNode<String>> stack = new Stack<>();
        for(GraphNode<String> node: graph)
        {
            if(node.neighbors.size()<K)
                stack.push(node);
        }
        while(graph.size()!=stack.size())
        {
            //least cost nodes
        }
        while(!stack.isEmpty())
        {
            GraphNode<String> node = stack.pop();
            List<Integer> possibleColors = new ArrayList<>();
            for(int i =0; i<K; i++)
                possibleColors.add(i);
            for(GraphNode<String> neighbor: node.neighbors)
            {
                if(neighbor.color!=-1)
                    possibleColors.remove(Integer.valueOf(neighbor.color));
            }
            if(possibleColors.size()==0)
            {
               node.color = -1; //indicates a spilled value
            }
            else
            {
                node.color = possibleColors.get(0);
            }
        }
    }

    public int getColor(String s)
    {
        for(GraphNode<String> node:graph)
        {
            if(s.equals(node.data))
                return node.color;
        }
        return -1;
    }

    private class GraphNode<T>
    {
        T data;
        List<GraphNode<T>> neighbors;
        int color;

        public GraphNode(T var)
        {
            data = var;
            color = -1;
        }
        public boolean equals(Object o)
        {
            if(o instanceof GraphNode)
            {
                GraphNode<String> node = (GraphNode<String>)o;
                if(node.data.equals(data))
                    return true;
            }
            return false;

        }
    }
}
