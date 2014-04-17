package edu.gatech.generation.allocation;

import java.util.*;

/**
 * Created by Alex on 4/16/14.
 */
public class InterferenceGraph
{
    private Map<String, GraphNode> graph;

    private final int UNUSED = -1;

    public InterferenceGraph(){
        graph = new LinkedHashMap<>();
    }

    public void addEdge(String source, String dest){
        if(graph.containsKey(source) && graph.containsKey(dest)){
            GraphNode foo = graph.get(source);
            GraphNode bar = graph.get(dest);
            foo.addNeighbor(bar);
            bar.addNeighbor(foo);
        }
    }

    public void addNode(String data){
        graph.put(data, new GraphNode(data));
    }

    public void colorGraph(int K){

        Stack<GraphNode> stack = new Stack<>();

        List<GraphNode> unused = new ArrayList<>();
        unused.addAll(graph.values());
        List<GraphNode> remove = new ArrayList<>();

        for(GraphNode node: unused)
            if(node.neighbors.size()<K){
                stack.push(node);
                remove.add(node);
            }

        unused.removeAll(remove);

        Collections.sort(unused);

        stack.addAll(unused);

        while(!stack.isEmpty())
        {
            GraphNode node = stack.pop();

            List<Integer> possibleColors = new ArrayList<>();

            for(int i =0; i<K; i++)
                possibleColors.add(i);

            for(GraphNode neighbor: node.neighbors)
                if(neighbor.color!=UNUSED)
                    possibleColors.remove(Integer.valueOf(neighbor.color));

            if(possibleColors.isEmpty())
               node.color = UNUSED; //indicates a spilled value
            else
                node.color = possibleColors.get(0);

        }
    }

    public int getColor(String s){
        return graph.get(s).color;
    }

    private class GraphNode implements Comparable< GraphNode>{
        private String data;
        private Set<GraphNode> neighbors;
        private int color;

        public GraphNode(String var){
            neighbors = new LinkedHashSet<>();
            data = var;
            color = UNUSED;
        }

        public void addNeighbor(GraphNode node){
            if(null != node)
                neighbors.add(node);
        }

        @Override
        public int compareTo(GraphNode other){
         return neighbors.size() - other.neighbors.size();
        }

        @Override
        public int hashCode(){
            return data.hashCode();
        }

        @Override
        public boolean equals(Object o){
         if(null == o) return false;
         if(this == o) return true;
         if(!(o instanceof GraphNode)) return false;
         GraphNode temp = (GraphNode) o;
         return temp.data.equals(data);
        }


    }
}
