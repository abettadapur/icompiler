package edu.gatech.generation.allocation;

import edu.gatech.generation.MipsOperation;
import edu.gatech.generation.MipsOperator;
import edu.gatech.generation.controlflow.BasicBlock;
import edu.gatech.generation.controlflow.ControlFlowGraphFactory;
import edu.gatech.intermediate.Intermediate;
import edu.gatech.intermediate.IntermediateOperation;
import edu.gatech.intermediate.OperationType;
import edu.gatech.intermediate.Operator;
import edu.gatech.util.Pair;
import edu.gatech.util.Util;

import java.util.*;

/**
 * Created by Alex on 4/4/14.
 */
public class BasicAllocator implements IAllocator
{

    private List<BasicBlock> entryPoints;

    @Override
    public void annotateIr(List<IntermediateOperation> irStream)
    {
        entryPoints = ControlFlowGraphFactory.getBasicBlocks(irStream);
        ControlFlowGraphFactory.printGraph(entryPoints);
        for(BasicBlock b: entryPoints){
            fillInOutSets(b);
            annotateBlock(b);
        }


    }

    private void annotateBlock(BasicBlock b)
    {
        InterferenceGraph graph = new InterferenceGraph();
        HashMap<String, Pair<Integer, Integer>> liveRangeTable = new HashMap<>();
        Set<String> buffer = new LinkedHashSet<>();
        //Set<String> dead = new LinkedHashSet<>();
        Set<String> prevIn = null;

        for(int i = 0; i<b.getContents().size(); i++)
        {
            Set<String> in = b.getContents().get(i).getIn();
            for(String s:in)
            {
                graph.addNode(s);
                for(String s2: in)
                {
                    if(!s.equals(s2))
                        graph.addEdge(s, s2);
                }
                if(!liveRangeTable.containsKey(s))
                {
                    liveRangeTable.put(s, new Pair<Integer,Integer>(i,-1));
                }
            }
            Set<String> dead = new HashSet<>();
            if(prevIn!=null)
                dead.addAll(prevIn);
            if(!dead.isEmpty())
            {
                dead.removeAll(in);//dead now contains the variables that died in the last instruction
                for(String s: dead)
                {
                    if(liveRangeTable.containsKey(s))
                        liveRangeTable.get(s).setU(i-1);
                }
            }

            prevIn = in;
        }
        graph.colorGraph(5); //TODO: ABSTRACT
        for(Map.Entry<String, Pair<Integer, Integer>> entry:liveRangeTable.entrySet())
        {
            int color = graph.getColor(entry.getKey());
            int registerNumber = -1;
            if(color!=InterferenceGraph.SPILLED)
                registerNumber = color+8;

            if(registerNumber!=-1)
            {
                int loadIndex = entry.getValue().getT();
                int storeIndex = entry.getValue().getU();

                IntermediateOperation load = new IntermediateOperation(Operator.LOAD, "", "$"+registerNumber,entry.getKey(), "", null);
                IntermediateOperation store = new IntermediateOperation(Operator.STORE, "",entry.getKey(),"$"+registerNumber, "", null);


            }



        }






    }

    private void fillInOutSets(BasicBlock block)
    {
        Set<String> previousIn=null;
        for(int i=block.getContents().size()-1; i>-1; i--)
        {
            IntermediateOperation operation = block.getContents().get(i);
            if(previousIn!=null)
            {
                operation.getOut().addAll(previousIn); //out[b] = in[b+1]
            }

            operation.getIn().addAll(operation.getOut());
            operation.getIn().removeAll(operation.getDef());
            operation.getIn().addAll(operation.getUse());
            previousIn = operation.getIn();
        }
        block.getIn().addAll(previousIn);
    }

    private Set<String> getVariables(BasicBlock b)
    {
        Set<String> variables = new HashSet<>();
        for(IntermediateOperation op:b.getContents())
        {
            variables.addAll(getOperands(op));
        }
        return variables;
    }

    private Set<String> getOperands(IntermediateOperation op)
    {
        Set<String> variables = new HashSet<>();
        if(op.getType()!=null)
        {
            switch(op.getType())
            {
                case BINARY:
                    if(!Util.isNumeric(op.getX()))
                        variables.add(op.getX());
                    if(!Util.isNumeric(op.getY()))
                        variables.add(op.getY());
                    if(!Util.isNumeric(op.getZ()))
                        variables.add(op.getZ());
                    break;
                case ASSIGN:
                    if(!Util.isNumeric(op.getX()))
                        variables.add(op.getX());
                    if(!Util.isNumeric(op.getY()))
                        variables.add(op.getY());
                    break;
                case GOTO:
                    break;
                case BRANCH:
                    if(!Util.isNumeric(op.getX()))
                        variables.add(op.getX());
                    if(!Util.isNumeric(op.getY()))
                        variables.add(op.getY());
                    break;
                case RETURN:
                    if(!Util.isNumeric(op.getX()))
                        variables.add(op.getX());
                case FUNCTION:
                    if(op.getParameters().size()!=0)
                    {
                        for(String s:op.getParameters())
                            if(!Util.isNumeric(s))
                                variables.add(s);
                    }
                    break;
                case FUNCTIONR:
                    if(!Util.isNumeric(op.getX()))
                        variables.add(op.getX());
                    if(op.getParameters().size()!=0)
                    {
                        for(String s:op.getParameters())
                            if(!Util.isNumeric(s))
                                variables.add(s);
                    }
                    break;
                case ARRAYSTORE:
                    if(!Util.isNumeric(op.getX()))
                        variables.add(op.getX());
                    if(!Util.isNumeric(op.getY()))
                        variables.add(op.getY());
                    if(!Util.isNumeric(op.getZ()))
                        variables.add(op.getZ());
                    break;
                case ARRAYLOAD:
                    if(!Util.isNumeric(op.getX()))
                        variables.add(op.getX());

                    if(!Util.isNumeric(op.getZ()))
                        variables.add(op.getZ());
                    break;
            }
        }
        return variables;
    }


}
