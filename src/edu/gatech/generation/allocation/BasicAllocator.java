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

        irStream.clear();
        for(BasicBlock b: entryPoints)
        {
            irStream.addAll(b.getContents());
        }


    }

    private void annotateBlock(BasicBlock b)
    {
        InterferenceGraph graph = new InterferenceGraph();
        HashMap<String, List<Pair<Integer, Integer>>> liveRangeTable = new HashMap<>();
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
               /* if(!liveRangeTable.containsKey(s))
                {
                    List<Pair<Integer,Integer>> ranges = new ArrayList<>();
                    ranges.add(new Pair<Integer,Integer>(i,-1));
                    liveRangeTable.put(s, ranges);
                }*/
            }
            Set<String> live = new HashSet<>();
            Set<String> dead = new HashSet<>();
            if(prevIn!=null)
            {
                dead.addAll(prevIn);
            }
            if(in!=null)
            {
                live.addAll(in);
            }
            if(!dead.isEmpty())
            {
                dead.removeAll(in);//dead now contains the variables that died in the last instruction
                for(String s: dead)
                {
                    if(liveRangeTable.containsKey(s))
                    {
                        List<Pair<Integer,Integer>> ranges = liveRangeTable.get(s);
                        ranges.get(ranges.size()-1).setU(i-1);
                    }
                }
            }
            if(!live.isEmpty())
            {
                if(prevIn!=null)
                    live.removeAll(prevIn);
                for(String s: live)
                {
                    List<Pair<Integer,Integer>> ranges = null;
                    if(!liveRangeTable.containsKey(s))
                    {
                        ranges = new ArrayList<>();

                    }
                    else
                    {
                       ranges = liveRangeTable.get(s);
                    }
                    ranges.add(new Pair<Integer,Integer>(i,-1));
                    liveRangeTable.put(s, ranges);
                }
            }

            prevIn = in;
        }
        graph.colorGraph(5); //TODO: ABSTRACT


        List<Pair<Integer, IntermediateOperation>> toInsert = new ArrayList<>();
        for(Map.Entry<String, List<Pair<Integer, Integer>>> entry:liveRangeTable.entrySet())
        {
            int color = graph.getColor(entry.getKey());
            int registerNumber = -1;
            if(color!=InterferenceGraph.SPILLED)
                registerNumber = color+8;

            if(registerNumber!=-1)
            {
                for(Pair<Integer, Integer> pair:entry.getValue())
                {
                    IntermediateOperation load = new IntermediateOperation(Operator.LOAD, "$"+registerNumber,entry.getKey(), "","", null);
                    IntermediateOperation store = new IntermediateOperation(Operator.STORE,entry.getKey(),"$"+registerNumber, "","", null);
                    if(pair.getU()==-1)
                        pair.setU(b.getContents().size()-1);
                    toInsert.add(new Pair<>(pair.getT(), load));
                    toInsert.add(new Pair<>(pair.getU(), store));
                }
                for(IntermediateOperation op: b.getContents())
                    op.registerReplace(entry.getKey(), "$"+registerNumber);
            }
            else
            {
               //spills
            }
        }
        Collections.sort(toInsert, new Comparator<Pair<Integer, IntermediateOperation>>() {
            @Override
            public int compare(Pair<Integer, IntermediateOperation> o1, Pair<Integer, IntermediateOperation> o2)
            {
                int diff = o1.getT() - o2.getT();
                if(diff==0)
                {
                    if(o1.getU().getOp()==o2.getU().getOp())
                        return 0;
                    else if(o1.getU().getOp()==Operator.LOAD)
                        return -1;
                    else
                        return 1;

                }
                return diff;
            }
        });

        int offset = 0;
        for(Pair<Integer, IntermediateOperation> op:toInsert)
        {
            if(op.getU().getOp()==Operator.LOAD)
                b.getContents().add(op.getT()+offset, op.getU());
            else
            {
                if(b.getContents().get(op.getT()+offset).getType()==OperationType.BRANCH)
                    b.getContents().add(op.getT()+offset, op.getU());
                else
                    b.getContents().add(op.getT()+offset+1, op.getU());
            }
            offset++;
        }

        for(int i = 0; i<b.getContents().size(); i++)
        {

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
